package com.episeerr.app.ui.screens.movierules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.model.RadarrMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MoviesBrowserUiState(
    val isLoading: Boolean = true,
    val movies: List<RadarrMovie> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val assigningMovieId: Int? = null,
    val filterRuleName: String? = null,
    val showAllMovies: Boolean = false
)

@HiltViewModel
class MoviesBrowserViewModel @Inject constructor(
    private val repository: EpiseerrRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filterRuleName: String? = savedStateHandle.get<String>("ruleName")

    private val _uiState = MutableStateFlow(
        MoviesBrowserUiState(filterRuleName = filterRuleName, showAllMovies = filterRuleName == null)
    )
    val uiState: StateFlow<MoviesBrowserUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.getRadarrMovies()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    movies = result.data.movies
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onShowAllChange(showAll: Boolean) {
        _uiState.value = _uiState.value.copy(showAllMovies = showAll)
    }

    fun assign(movieId: Int, ruleName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(assigningMovieId = movieId)
            when (val result = repository.assignMovieRule(movieId, ruleName)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    assigningMovieId = null,
                    movies = _uiState.value.movies.map {
                        if (it.id == movieId) it.copy(assignedRule = result.data.assignedRule) else it
                    }
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    assigningMovieId = null,
                    error = result.message
                )
            }
        }
    }
}
