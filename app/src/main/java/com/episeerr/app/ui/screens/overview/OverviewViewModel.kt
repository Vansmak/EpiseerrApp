package com.episeerr.app.ui.screens.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.model.ActivityItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import javax.inject.Inject

data class OverviewServiceRow(
    val key: String,
    val displayName: String,
    val connected: Boolean,
    val enabled: Boolean,
    val configurable: Boolean,
    val statLines: List<String>
)

data class OverviewUiState(
    val isLoading: Boolean = true,
    val setupComplete: Boolean = false,
    val services: List<OverviewServiceRow> = emptyList(),
    val activity: List<ActivityItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repository: EpiseerrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val statsResult = repository.getDashboardStats()
            val activityResult = repository.getDashboardActivity()
            val schemaResult = repository.getSetupSchema()

            val stats = (statsResult as? ApiResult.Success)?.data?.stats ?: JsonObject(emptyMap())
            val schema = (schemaResult as? ApiResult.Success)?.data

            val keys = (stats.keys + listOfNotNull("sonarr", "tmdb") + (schema?.integrations?.keys ?: emptySet())).distinct()

            val rows = keys.map { key ->
                val statObj = stats[key] as? JsonObject
                val statConfigured = (statObj?.get("configured") as? JsonPrimitive)?.booleanOrNull
                val statLines = statObj?.entries
                    ?.mapNotNull { (k, v) -> if (k == "configured") null else (v as? JsonPrimitive)?.let { "$k: ${it.content}" } }
                    ?: emptyList()

                when (key) {
                    "sonarr" -> OverviewServiceRow(
                        key, "Sonarr",
                        connected = schema?.sonarr?.connected ?: statConfigured ?: false,
                        enabled = schema?.sonarr?.enabled ?: true,
                        configurable = true,
                        statLines = statLines
                    )
                    "tmdb" -> OverviewServiceRow(
                        key, "TMDB",
                        connected = schema?.tmdb?.connected ?: statConfigured ?: false,
                        enabled = schema?.tmdb?.enabled ?: true,
                        configurable = true,
                        statLines = statLines
                    )
                    else -> {
                        val integration = schema?.integrations?.get(key)
                        OverviewServiceRow(
                            key,
                            integration?.displayName ?: key.replaceFirstChar { it.uppercase() },
                            connected = integration?.connected ?: statConfigured ?: true,
                            enabled = integration?.enabled ?: true,
                            configurable = integration != null,
                            statLines = statLines
                        )
                    }
                }
            }.sortedWith(compareBy({ !it.connected }, { it.displayName }))

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                setupComplete = schema?.setupComplete ?: false,
                services = rows,
                activity = (activityResult as? ApiResult.Success)?.data?.services ?: emptyList(),
                error = (statsResult as? ApiResult.Error)?.message
            )
        }
    }
}
