package com.episeerr.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.episeerr.app.ui.EpiseerrApp
import com.episeerr.app.ui.theme.EpiseerrTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EpiseerrTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EpiseerrApp()
                }
            }
        }
    }
}
