package com.cmt.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.osmdroid.util.GeoPoint
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private val httpClient by lazy { OkHttpClient() }

fun getGpxUrlForRace(raceId: String): String {
    return "https://example.com/routes/$raceId.gpx"
}

suspend fun downloadGpxIfMissing(url: String, destFile: File) {
    if (fileExistsAndHasSize(destFile)) {
        Log.i("GpxRepository", "GPX already cached at ${destFile.absolutePath}")
        return
    }

    withContext(Dispatchers.IO) {
        destFile.parentFile?.let { parent ->
            if (!parent.exists()) {
                val created = parent.mkdirs()
                if (!created) {
                    Log.w("GpxRepository", "Could not create directory ${parent.absolutePath}")
                }
            }
        }

        val request = Request.Builder().url(url).build()
        val tempFile = File(destFile.parentFile, "${destFile.name}.download")
        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("GpxRepository", "Failed to download GPX: HTTP ${response.code}")
                    return@withContext
                }
                val body = response.body ?: run {
                    Log.e("GpxRepository", "GPX response body is null")
                    return@withContext
                }
                FileOutputStream(tempFile).use { output ->
                    body.byteStream().use { input ->
                        input.copyTo(output)
                    }
                }
            }

            if (!fileExistsAndHasSize(tempFile)) {
                Log.e("GpxRepository", "Downloaded GPX empty; removing temp file")
                tempFile.delete()
                return@withContext
            }

            if (!tempFile.renameTo(destFile)) {
                Log.e("GpxRepository", "Failed to move GPX temp file to destination")
            } else {
                Log.i("GpxRepository", "GPX saved to ${destFile.absolutePath}")
            }
        } catch (e: IOException) {
            Log.e("GpxRepository", "Error downloading GPX: ${e.message}", e)
            tempFile.delete()
        } catch (e: Exception) {
            Log.e("GpxRepository", "Unexpected GPX download error", e)
            tempFile.delete()
        }
    }
}

fun parseGpxTrackPoints(file: File): List<GeoPoint> {
    if (!fileExistsAndHasSize(file)) return emptyList()
    return try {
        val factory = XmlPullParserFactory.newInstance().apply {
            isNamespaceAware = true
        }
        val parser = factory.newPullParser()
        file.inputStream().use { input ->
            parser.setInput(input, null)
            val points = mutableListOf<GeoPoint>()
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "trkpt") {
                    val lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                    val lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        points.add(GeoPoint(lat, lon))
                    }
                }
                eventType = parser.next()
            }
            points
        }
    } catch (e: Exception) {
        Log.e("GpxRepository", "Error parsing GPX: ${e.message}", e)
        emptyList()
    }
}
