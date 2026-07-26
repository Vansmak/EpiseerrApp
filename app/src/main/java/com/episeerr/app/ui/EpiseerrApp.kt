package com.episeerr.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.episeerr.app.data.PreferencesRepository
import com.episeerr.app.data.SessionManager
import com.episeerr.app.ui.screens.connect.ConnectScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface AppEntryPoint {
    fun preferencesRepository(): PreferencesRepository
    fun sessionManager(): SessionManager
}

/**
 * Top-level gate: shows Connect/Login until a server URL is saved and the session isn't
 * flagged as unauthorized, then hands off to the bottom-nav main shell. Both conditions are
 * backed by reactive state (DataStore Flow / SessionManager StateFlow), so entering a URL or
 * logging in successfully recomposes straight into MainScaffold with no manual navigation call.
 */
@Composable
fun EpiseerrApp() {
    val context = LocalContext.current
    val entryPoint = remember {
        EntryPointAccessors.fromApplication(context.applicationContext, AppEntryPoint::class.java)
    }

    val serverUrl by entryPoint.preferencesRepository().serverUrlFlow.collectAsState(initial = null)
    val needsLogin by entryPoint.sessionManager().needsLogin.collectAsState()

    when {
        serverUrl == null -> {
            // Still loading the saved server URL from DataStore - render nothing this frame.
        }
        serverUrl!!.isBlank() || needsLogin -> {
            ConnectScreen(onConnected = {})
        }
        else -> {
            MainScaffold()
        }
    }
}
