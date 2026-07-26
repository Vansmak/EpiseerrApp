package com.episeerr.app.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceRow(
    val key: String,
    val displayName: String,
    val connected: Boolean,
    val enabled: Boolean
)

data class ServicesUiState(
    val isLoading: Boolean = true,
    val setupComplete: Boolean = false,
    val services: List<ServiceRow> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val repository: EpiseerrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.getSetupSchema()) {
                is ApiResult.Success -> {
                    val data = result.data
                    val rows = buildList {
                        add(ServiceRow("sonarr", "Sonarr", data.sonarr?.connected ?: false, data.sonarr?.enabled ?: false))
                        add(ServiceRow("tmdb", "TMDB", data.tmdb?.connected ?: false, data.tmdb?.enabled ?: false))
                        data.integrations.values.forEach { integration ->
                            add(
                                ServiceRow(
                                    key = integration.serviceName,
                                    displayName = integration.displayName,
                                    connected = integration.connected,
                                    enabled = integration.enabled
                                )
                            )
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        setupComplete = data.setupComplete,
                        services = rows
                    )
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun toggle(service: String, enabled: Boolean) {
        viewModelScope.launch {
            // Optimistic update so the switch feels immediate; refresh() reconciles either way.
            _uiState.value = _uiState.value.copy(
                services = _uiState.value.services.map {
                    if (it.key == service) it.copy(enabled = enabled) else it
                }
            )
            when (repository.toggleService(service, enabled)) {
                is ApiResult.Success -> refresh()
                is ApiResult.Error -> refresh()
            }
        }
    }
}
