package com.episeerr.app.data

import com.episeerr.app.data.api.EpiseerrApi
import com.episeerr.app.data.model.ApplySelectionRuleRequest
import com.episeerr.app.data.model.ApplySelectionRuleResult
import com.episeerr.app.data.model.ApproveResult
import com.episeerr.app.data.model.AssignMovieRuleRequest
import com.episeerr.app.data.model.AssignMovieRuleResponse
import com.episeerr.app.data.model.AssignSeriesRuleRequest
import com.episeerr.app.data.model.AssignSeriesRuleResponse
import com.episeerr.app.data.model.DashboardActivityResponse
import com.episeerr.app.data.model.DashboardStatsResponse
import com.episeerr.app.data.model.DeleteResponse
import com.episeerr.app.data.model.EpisodeIdsRequest
import com.episeerr.app.data.model.LogsResponse
import com.episeerr.app.data.model.MovieIdsRequest
import com.episeerr.app.data.model.PendingDeletionsResponse
import com.episeerr.app.data.model.PendingRequestsResponse
import com.episeerr.app.data.model.PendingWatchEventsResponse
import com.episeerr.app.data.model.ProcessWatchEventResult
import com.episeerr.app.data.model.RadarrMoviesResponse
import com.episeerr.app.data.model.RejectResult
import com.episeerr.app.data.model.SaveServiceResponse
import com.episeerr.app.data.model.SeriesListResponse
import com.episeerr.app.data.model.SimpleSuccessResponse
import com.episeerr.app.data.model.GlobalSettings
import com.episeerr.app.data.model.GlobalSettingsResponse
import com.episeerr.app.data.model.MovieRuleRequest
import com.episeerr.app.data.model.MovieRuleResponse
import com.episeerr.app.data.model.MovieRulesListResponse
import com.episeerr.app.data.model.RuleRequest
import com.episeerr.app.data.model.RuleResponse
import com.episeerr.app.data.model.RulesListResponse
import com.episeerr.app.data.model.SetupSchemaResponse
import com.episeerr.app.data.model.ToggleServiceRequest
import com.episeerr.app.data.model.ToggleServiceResponse
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpiseerrRepository @Inject constructor(
    private val api: EpiseerrApi
) {
    suspend fun login(username: String, password: String): ApiResult<Boolean> = apiCall {
        val response = api.login(username, password)
        response.isSuccessful
    }

    suspend fun getDashboardStats(): ApiResult<DashboardStatsResponse> = apiCall { api.getDashboardStats() }

    suspend fun getDashboardActivity(): ApiResult<DashboardActivityResponse> = apiCall { api.getDashboardActivity() }

    suspend fun getRulesList(): ApiResult<RulesListResponse> = apiCall { api.getRulesList() }

    suspend fun getRule(name: String): ApiResult<RuleResponse> = apiCall { api.getRule(name) }

    suspend fun createRule(body: RuleRequest): ApiResult<RuleResponse> = apiCall { api.createRule(body) }

    suspend fun updateRule(name: String, body: RuleRequest): ApiResult<RuleResponse> =
        apiCall { api.updateRule(name, body) }

    suspend fun deleteRule(name: String): ApiResult<DeleteResponse> = apiCall { api.deleteRule(name) }

    suspend fun getMovieRulesList(): ApiResult<MovieRulesListResponse> = apiCall { api.getMovieRulesList() }

    suspend fun getMovieRule(name: String): ApiResult<MovieRuleResponse> = apiCall { api.getMovieRule(name) }

    suspend fun createMovieRule(body: MovieRuleRequest): ApiResult<MovieRuleResponse> =
        apiCall { api.createMovieRule(body) }

    suspend fun updateMovieRule(name: String, body: MovieRuleRequest): ApiResult<MovieRuleResponse> =
        apiCall { api.updateMovieRule(name, body) }

    suspend fun deleteMovieRule(name: String): ApiResult<DeleteResponse> = apiCall { api.deleteMovieRule(name) }

    suspend fun getSeriesList(): ApiResult<SeriesListResponse> = apiCall { api.getSeriesList() }

    suspend fun assignSeriesRule(seriesId: Int, ruleName: String): ApiResult<AssignSeriesRuleResponse> =
        apiCall { api.assignSeriesRule(AssignSeriesRuleRequest(seriesId, ruleName)) }

    suspend fun getRadarrMovies(): ApiResult<RadarrMoviesResponse> = apiCall { api.getRadarrMovies() }

    suspend fun assignMovieRule(movieId: Int, ruleName: String): ApiResult<AssignMovieRuleResponse> =
        apiCall { api.assignMovieRule(AssignMovieRuleRequest(movieId, ruleName)) }

    suspend fun getSetupSchema(): ApiResult<SetupSchemaResponse> = apiCall { api.getSetupSchema() }

    suspend fun toggleService(service: String, enabled: Boolean): ApiResult<ToggleServiceResponse> =
        apiCall { api.toggleService(service, ToggleServiceRequest(enabled)) }

    suspend fun saveService(service: String, fields: JsonObject): ApiResult<SaveServiceResponse> =
        apiCall { api.saveService(service, fields) }

    suspend fun testConnection(service: String, fields: JsonObject): ApiResult<SaveServiceResponse> =
        apiCall { api.testConnection(service, fields) }

    suspend fun getLogs(logFile: String, lines: Int, level: String, search: String): ApiResult<LogsResponse> =
        apiCall { api.getLogs(logFile, lines, level, search) }

    suspend fun getGlobalSettings(): ApiResult<GlobalSettingsResponse> = apiCall { api.getGlobalSettings() }

    suspend fun updateGlobalSettings(settings: GlobalSettings): ApiResult<GlobalSettingsResponse> =
        apiCall { api.updateGlobalSettings(settings) }

    suspend fun getPendingDeletions(): ApiResult<PendingDeletionsResponse> =
        apiCall { api.getPendingDeletions() }

    suspend fun approveEpisodeDeletions(episodeIds: List<Int>): ApiResult<ApproveResult> =
        apiCall { api.approveEpisodeDeletions(EpisodeIdsRequest(episodeIds)) }

    suspend fun rejectEpisodeDeletions(episodeIds: List<Int>): ApiResult<RejectResult> =
        apiCall { api.rejectEpisodeDeletions(EpisodeIdsRequest(episodeIds)) }

    suspend fun approveMovieDeletions(movieIds: List<Int>): ApiResult<ApproveResult> =
        apiCall { api.approveMovieDeletions(MovieIdsRequest(movieIds)) }

    suspend fun rejectMovieDeletions(movieIds: List<Int>): ApiResult<RejectResult> =
        apiCall { api.rejectMovieDeletions(MovieIdsRequest(movieIds)) }

    suspend fun getPendingWatchEvents(): ApiResult<PendingWatchEventsResponse> =
        apiCall { api.getPendingWatchEvents() }

    suspend fun processPendingWatchEvent(itemId: String): ApiResult<ProcessWatchEventResult> =
        apiCall { api.processPendingWatchEvent(itemId) }

    suspend fun clearPendingWatchEvent(itemId: String): ApiResult<SimpleSuccessResponse> =
        apiCall { api.clearPendingWatchEvent(itemId) }

    suspend fun clearAllPendingWatchEvents(): ApiResult<SimpleSuccessResponse> =
        apiCall { api.clearAllPendingWatchEvents() }

    suspend fun getPendingRequests(): ApiResult<PendingRequestsResponse> =
        apiCall { api.getPendingRequests() }

    suspend fun applyRuleToSelection(tmdbId: String, ruleName: String): ApiResult<ApplySelectionRuleResult> =
        apiCall { api.applyRuleToSelection(ApplySelectionRuleRequest(tmdbId, ruleName)) }

    suspend fun deletePendingRequest(requestId: String): ApiResult<SaveServiceResponse> =
        apiCall { api.deletePendingRequest(requestId) }
}
