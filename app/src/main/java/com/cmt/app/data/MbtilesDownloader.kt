package com.cmt.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

const val MBTILES_PLACEHOLDER_URL = "https://example.com/path/to/corfu.mbtiles"
const val MBTILES_FILE_NAME = "corfu.mbtiles"

fun fileExistsAndHasSize(file: File, minBytes: Long = 1L): Boolean {
    return file.exists() && file.length() >= minBytes
}

suspend fun downloadFileIfMissing(url: String, destFile: File) {
    if (fileExistsAndHasSize(destFile)) {
        Log.i("MbtilesDownloader", "File already exists at ${destFile.absolutePath}")
        return
    }

    withContext(Dispatchers.IO) {
        val parent = destFile.parentFile
        if (parent != null && !parent.exists()) {
            val created = parent.mkdirs()
            if (!created) {
                Log.w("MbtilesDownloader", "Could not create directory ${parent.absolutePath}")
            }
        }

        var connection: HttpURLConnection? = null
        val tempFile = File(destFile.parentFile, "${destFile.name}.download")
        try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 20_000
                requestMethod = "GET"
                doInput = true
                connect()
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("MbtilesDownloader", "Failed to download file: HTTP $responseCode")
                return@withContext
            }

            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (!fileExistsAndHasSize(tempFile)) {
                Log.e("MbtilesDownloader", "Downloaded file is empty; deleting temp file")
                tempFile.delete()
                return@withContext
            }

            if (tempFile.renameTo(destFile)) {
                Log.i("MbtilesDownloader", "Downloaded MBTiles to ${destFile.absolutePath}")
            } else {
                Log.e("MbtilesDownloader", "Failed to move temp file to destination")
            }
        } catch (e: IOException) {
            Log.e("MbtilesDownloader", "Error downloading MBTiles: ${e.message}", e)
            tempFile.delete()
        } catch (e: Exception) {
            Log.e("MbtilesDownloader", "Unexpected error downloading MBTiles", e)
            tempFile.delete()
        } finally {
            connection?.disconnect()
        }
    }
}
