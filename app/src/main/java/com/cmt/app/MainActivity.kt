package com.cmt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.cmt.app.navigation.CMTNavHost
import com.cmt.app.ui.theme.CMTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CMTTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CMTNavHost()
                }
            }
        }
    }
}
