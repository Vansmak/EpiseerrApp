package com.episeerr.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "episeerr_settings")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SERVER_URL_KEY = stringPreferencesKey("server_url")
    }

    val serverUrlFlow: Flow<String> = context.dataStore.data.map { it[SERVER_URL_KEY] ?: "" }

    suspend fun getServerUrl(): String = serverUrlFlow.first()

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[SERVER_URL_KEY] = url.trim().trimEnd('/') }
    }
}
