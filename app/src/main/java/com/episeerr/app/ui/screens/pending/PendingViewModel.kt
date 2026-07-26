package com.episeerr.app.ui.screens.pending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.model.PendingEpisodesSummary
import com.episeerr.app.data.model.PendingMoviesSummary
import com.episeerr.app.data.model.PendingRequestItem
import com.episeerr.app.data.model.PendingWatchEventItem
import com.episeerr.app.data.model.RuleSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject

data class PendingUiState(
    val isLoading: Boolean = true,
    val episodesSummary: PendingEpisodesSummary = PendingEpisodesSummary(),
    val moviesSummary: PendingMoviesSummary = PendingMoviesSummary(),
    val watchEvents: List<PendingWatchEventItem> = emptyList(),
    val selectionRequests: List<PendingRequestItem> = emptyList(),
    val availableRules: List<RuleSummary> = emptyList(),
    val selectedEpisodeIds: Set<Int> = emptySet(),
    val selectedMovieIds: Set<Int> = emptySet(),
    val isActing: Boolean = false,
    val error: String? = null
)

/** tmdb_id can come back as either a JSON string or number depending on source - normalize to String. */
fun PendingRequestItem.tmdbIdString(): String = (tmdbId as? JsonPrimitive)?.content ?: ""

@HiltViewModel
class PendingViewModel @Inject constructor(
    private val repository: EpiseerrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendingUiState())
    val uiState: StateFlow<PendingUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val deletionsResult = repository.getPendingDeletions()
            val watchEventsResult = repository.getPendingWatchEvents()
            val requestsResult = repository.getPendingRequests()
            val rulesResult = repository.getRulesList()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                episodesSummary = (deletionsResult as? ApiResult.Success)?.data?.episodes ?: PendingEpisodesSummary(),
                moviesSummary = (deletionsResult as? ApiResult.Success)?.data?.movies ?: PendingMoviesSummary(),
                watchEvents = (watchEventsResult as? ApiResult.Success)?.data?.items ?: emptyList(),
                selectionRequests = (requestsResult as? ApiResult.Success)?.data?.requests ?: emptyList(),
                availableRules = (rulesResult as? ApiResult.Success)?.data?.rules ?: emptyList(),
                selectedEpisodeIds = emptySet(),
                selectedMovieIds = emptySet(),
                error = (deletionsResult as? ApiResult.Error)?.message
            )
        }
    }

    fun toggleEpisodeSelected(episodeId: Int) {
        val current = _uiState.value.selectedEpisodeIds
        _uiState.value = _uiState.value.copy(
            selectedEpisodeIds = if (episodeId in current) current - episodeId else current + episodeId
        )
    }

    fun toggleMovieSelected(movieId: Int) {
        val current = _uiState.value.selectedMovieIds
        _uiState.value = _uiState.value.copy(
            selectedMovieIds = if (movieId in current) current - movieId else current + movieId
        )
    }

    fun approveSelected() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isActing = true, error = null)
            if (state.selectedEpisodeIds.isNotEmpty()) {
                repository.approveEpisodeDeletions(state.selectedEpisodeIds.toList())
            }
            if (state.selectedMovieIds.isNotEmpty()) {
                repository.approveMovieDeletions(state.selectedMovieIds.toList())
            }
            _uiState.value = _uiState.value.copy(isActing = false)
            refresh()
        }
    }

    fun rejectSelected() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isActing = true, error = null)
            if (state.selectedEpisodeIds.isNotEmpty()) {
                repository.rejectEpisodeDeletions(state.selectedEpisodeIds.toList())
            }
            if (state.selectedMovieIds.isNotEmpty()) {
                repository.rejectMovieDeletions(state.selectedMovieIds.toList())
            }
            _uiState.value = _uiState.value.copy(isActing = false)
            refresh()
        }
    }

    fun processWatchEvent(itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActing = true)
            repository.processPendingWatchEvent(itemId)
            _uiState.value = _uiState.value.copy(isActing = false)
            refresh()
        }
    }

    fun clearWatchEvent(itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActing = true)
            repository.clearPendingWatchEvent(itemId)
            _uiState.value = _uiState.value.copy(isActing = false)
            refresh()
        }
    }

    fun clearAllWatchEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActing = true)
            repository.clearAllPendingWatchEvents()
            _uiState.value = _uiState.value.copy(isActing = false)
            refresh()
        }
    }

    fun applyRuleToSelection(tmdbId: String, ruleName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActing = true, error = null)
            val result = repository.applyRuleToSelection(tmdbId, ruleName)
            _uiState.value = _uiState.value.copy(
                isActing = false,
                error = (result as? ApiResult.Error)?.message
            )
            refresh()
        }
    }

    fun dismissSelection(requestId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActing = true)
            repository.deletePendingRequest(requestId)
            _uiState.value = _uiState.value.copy(isActing = false)
            refresh()
        }
    }
}
