package com.episeerr.app.ui.screens.rules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.model.SonarrSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesBrowserUiState(
    val isLoading: Boolean = true,
    val series: List<SonarrSeries> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val assigningSeriesId: Int? = null,
    val filterRuleName: String? = null,
    val showAllSeries: Boolean = false
)

@HiltViewModel
class SeriesBrowserViewModel @Inject constructor(
    private val repository: EpiseerrRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filterRuleName: String? = savedStateHandle.get<String>("ruleName")

    private val _uiState = MutableStateFlow(
        SeriesBrowserUiState(filterRuleName = filterRuleName, showAllSeries = filterRuleName == null)
    )
    val uiState: StateFlow<SeriesBrowserUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.getSeriesList()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    series = result.data.series
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onShowAllChange(showAll: Boolean) {
        _uiState.value = _uiState.value.copy(showAllSeries = showAll)
    }

    fun assign(seriesId: Int, ruleName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(assigningSeriesId = seriesId)
            when (val result = repository.assignSeriesRule(seriesId, ruleName)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    assigningSeriesId = null,
                    series = _uiState.value.series.map {
                        if (it.id == seriesId) it.copy(assignedRule = result.data.assignedRule) else it
                    }
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    assigningSeriesId = null,
                    error = result.message
                )
            }
        }
    }
}
