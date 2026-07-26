package com.episeerr.app.ui.screens.dashboard

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
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val stats: JsonObject = JsonObject(emptyMap()),
    val activity: List<ActivityItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: EpiseerrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val statsResult = repository.getDashboardStats()
            val activityResult = repository.getDashboardActivity()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                stats = (statsResult as? ApiResult.Success)?.data?.stats ?: JsonObject(emptyMap()),
                activity = (activityResult as? ApiResult.Success)?.data?.services ?: emptyList(),
                error = (statsResult as? ApiResult.Error)?.message
            )
        }
    }
}
