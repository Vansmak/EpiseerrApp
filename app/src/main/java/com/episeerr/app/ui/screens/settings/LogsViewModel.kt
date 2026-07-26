package com.episeerr.app.ui.screens.settings

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

private val DEFAULT_LOG_FILES = listOf("episeerr.log", "cleanup.log", "app.log")

data class LogsUiState(
    val isLoading: Boolean = true,
    val logFile: String = "episeerr.log",
    val availableLogs: List<String> = DEFAULT_LOG_FILES,
    val lines: List<String> = emptyList(),
    val totalLines: Int = 0,
    val logSize: String = "",
    val search: String = "",
    val error: String? = null
)

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val repository: EpiseerrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            when (val result = repository.getLogs(state.logFile, 300, "ALL", state.search)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lines = result.data.logLines,
                    totalLines = result.data.totalLines,
                    logSize = result.data.logSize,
                    availableLogs = result.data.availableLogs.ifEmpty { DEFAULT_LOG_FILES }
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun onLogFileChange(logFile: String) {
        _uiState.value = _uiState.value.copy(logFile = logFile)
        refresh()
    }

    fun onSearchChange(search: String) {
        _uiState.value = _uiState.value.copy(search = search)
    }
}
