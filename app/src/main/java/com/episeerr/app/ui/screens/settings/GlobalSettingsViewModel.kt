package com.episeerr.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.PreferencesRepository
import com.episeerr.app.data.model.GlobalSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GlobalSettingsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val storageMinGb: String = "",
    val cleanupIntervalHours: String = "6",
    val dryRunMode: Boolean = false,
    val autoAssignNewSeries: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val discordWebhookUrl: String = "",
    val automationHeld: Boolean = false,
    val reconcileEnabled: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

@HiltViewModel
class GlobalSettingsViewModel @Inject constructor(
    private val repository: EpiseerrRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GlobalSettingsUiState())
    val uiState: StateFlow<GlobalSettingsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.getGlobalSettings()) {
                is ApiResult.Success -> {
                    val settings = result.data.settings
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        storageMinGb = settings?.globalStorageMinGb?.toString() ?: "",
                        cleanupIntervalHours = settings?.cleanupIntervalHours?.toString() ?: "6",
                        dryRunMode = settings?.dryRunMode ?: false,
                        autoAssignNewSeries = settings?.autoAssignNewSeries ?: false,
                        notificationsEnabled = settings?.notificationsEnabled ?: false,
                        discordWebhookUrl = settings?.discordWebhookUrl ?: "",
                        automationHeld = settings?.automationHeld ?: false,
                        reconcileEnabled = settings?.reconcileEnabled ?: false
                    )
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun update(transform: GlobalSettingsUiState.() -> GlobalSettingsUiState) {
        _uiState.value = _uiState.value.transform().copy(error = null, saved = false)
    }

    fun save() {
        val state = _uiState.value
        val settings = GlobalSettings(
            globalStorageMinGb = state.storageMinGb.toIntOrNull(),
            cleanupIntervalHours = state.cleanupIntervalHours.toIntOrNull() ?: 6,
            dryRunMode = state.dryRunMode,
            autoAssignNewSeries = state.autoAssignNewSeries,
            notificationsEnabled = state.notificationsEnabled,
            discordWebhookUrl = state.discordWebhookUrl,
            automationHeld = state.automationHeld,
            reconcileEnabled = state.reconcileEnabled
        )
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            when (val result = repository.updateGlobalSettings(settings)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isSaving = false, error = result.message)
            }
        }
    }

    fun disconnectServer() {
        viewModelScope.launch {
            preferencesRepository.setServerUrl("")
        }
    }
}
