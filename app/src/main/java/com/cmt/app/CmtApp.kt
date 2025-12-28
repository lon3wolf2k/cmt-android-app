package com.cmt.app

import android.app.Application
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration

class CmtApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val context = applicationContext
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        Configuration.getInstance().userAgentValue = context.packageName
    }
}
