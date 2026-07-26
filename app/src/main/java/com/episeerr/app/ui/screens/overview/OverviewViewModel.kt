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
import javax.inject.Inject

data class OverviewServiceRow(
    val key: String,
    val displayName: String,
    val connected: Boolean,
    val enabled: Boolean
)

/**
 * Integrations that exist in setup-schema but aren't part of Episeerr's actual
 * media-management purpose (personal/hobby add-ons bundled into a custom instance).
 */
private val NON_CORE_SERVICE_KEYS = setOf("dispatcharr", "docker", "sonos", "spotify", "xadarr", "gameday", "game_day")

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

            val activityResult = repository.getDashboardActivity()
            val schemaResult = repository.getSetupSchema()

            when (schemaResult) {
                is ApiResult.Success -> {
                    val schema = schemaResult.data
                    val rows = buildList {
                        add(
                            OverviewServiceRow(
                                "sonarr", "Sonarr",
                                connected = schema.sonarr?.connected ?: false,
                                enabled = schema.sonarr?.enabled ?: true
                            )
                        )
                        add(
                            OverviewServiceRow(
                                "tmdb", "TMDB",
                                connected = schema.tmdb?.connected ?: false,
                                enabled = schema.tmdb?.enabled ?: true
                            )
                        )
                        schema.integrations.values
                            .filter { it.serviceName.lowercase() !in NON_CORE_SERVICE_KEYS }
                            .forEach { integration ->
                                add(
                                    OverviewServiceRow(
                                        integration.serviceName,
                                        integration.displayName,
                                        connected = integration.connected,
                                        enabled = integration.enabled
                                    )
                                )
                            }
                    }.sortedWith(compareBy({ !it.connected }, { it.displayName }))

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        setupComplete = schema.setupComplete,
                        services = rows,
                        activity = (activityResult as? ApiResult.Success)?.data?.services ?: emptyList()
                    )
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = schemaResult.message
                )
            }
        }
    }
}
