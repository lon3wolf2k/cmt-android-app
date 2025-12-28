package com.cmt.app

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import com.cmt.app.data.MBTILES_FILE_NAME
import com.cmt.app.data.MBTILES_PLACEHOLDER_URL
import com.cmt.app.data.downloadFileIfMissing
import com.cmt.app.data.fileExistsAndHasSize
import org.osmdroid.config.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

class CmtApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        val context = applicationContext
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        Configuration.getInstance().userAgentValue = context.packageName
        scheduleMbtilesDownload()
    }

    private fun scheduleMbtilesDownload() {
        val baseDir = getExternalFilesDir(null)
        if (baseDir == null) {
            Log.w("CmtApp", "External files directory unavailable; skipping MBTiles download")
            return
        }
        val mapsDir = File(baseDir, "maps")
        val mbtilesFile = File(mapsDir, MBTILES_FILE_NAME)
        if (fileExistsAndHasSize(mbtilesFile)) {
            Log.i("CmtApp", "MBTiles already present at ${mbtilesFile.absolutePath}")
            return
        }
        appScope.launch {
            downloadFileIfMissing(
                url = MBTILES_PLACEHOLDER_URL,
                destFile = mbtilesFile
            )
        }
    }
}
