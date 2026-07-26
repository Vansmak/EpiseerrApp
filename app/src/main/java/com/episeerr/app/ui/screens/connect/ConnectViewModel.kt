package com.episeerr.app.ui.screens.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.PreferencesRepository
import com.episeerr.app.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConnectUiState(
    val serverUrl: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val needsLogin: Boolean = false,
    val username: String = "",
    val password: String = "",
    val connected: Boolean = false
)

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val repository: EpiseerrRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectUiState())
    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(serverUrl = preferencesRepository.getServerUrl())
        }
    }

    fun onServerUrlChange(value: String) {
        _uiState.value = _uiState.value.copy(serverUrl = value, error = null)
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    /** Save the server URL, then probe it (a REQUIRE_AUTH=true server will 401 -> show login). */
    fun connect() {
        val url = _uiState.value.serverUrl.trim()
        if (url.isBlank() || (!url.startsWith("http://") && !url.startsWith("https://"))) {
            _uiState.value = _uiState.value.copy(error = "URL must start with http:// or https://")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            preferencesRepository.setServerUrl(url)
            sessionManager.markAuthenticated()

            when (val result = repository.getDashboardStats()) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, connected = true)
                }
                is ApiResult.Error -> {
                    val needsLogin = sessionManager.needsLogin.value
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        needsLogin = needsLogin,
                        connected = needsLogin, // login screen takes over from here
                        error = if (needsLogin) null else "Couldn't reach Episeerr: ${result.message}"
                    )
                }
            }
        }
    }

    fun login() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            when (val result = repository.login(state.username, state.password)) {
                is ApiResult.Success -> {
                    sessionManager.markAuthenticated()
                    _uiState.value = _uiState.value.copy(isLoading = false, needsLogin = false, connected = true)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Login failed: ${result.message}")
                }
            }
        }
    }
}
