package com.episeerr.app.data.api

import com.episeerr.app.data.model.ApplySelectionRuleRequest
import com.episeerr.app.data.model.ApplySelectionRuleResult
import com.episeerr.app.data.model.AssignMovieRuleRequest
import com.episeerr.app.data.model.AssignMovieRuleResponse
import com.episeerr.app.data.model.AssignSeriesRuleRequest
import com.episeerr.app.data.model.AssignSeriesRuleResponse
import com.episeerr.app.data.model.DashboardActivityResponse
import com.episeerr.app.data.model.DashboardStatsResponse
import com.episeerr.app.data.model.ApproveResult
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
import com.episeerr.app.data.model.SaveServiceResponse
import com.episeerr.app.data.model.SetupSchemaResponse
import com.episeerr.app.data.model.ToggleServiceRequest
import com.episeerr.app.data.model.ToggleServiceResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EpiseerrApi {

    @FormUrlEncoded
    @POST("/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Unit>

    @GET("/api/dashboard/stats")
    suspend fun getDashboardStats(): DashboardStatsResponse

    @GET("/api/dashboard/activity")
    suspend fun getDashboardActivity(): DashboardActivityResponse

    @GET("/api/rules-list")
    suspend fun getRulesList(): RulesListResponse

    @GET("/api/rules/{name}")
    suspend fun getRule(@Path("name") name: String): RuleResponse

    @POST("/api/rules")
    suspend fun createRule(@Body body: RuleRequest): RuleResponse

    @PUT("/api/rules/{name}")
    suspend fun updateRule(@Path("name") name: String, @Body body: RuleRequest): RuleResponse

    @DELETE("/api/rules/{name}")
    suspend fun deleteRule(@Path("name") name: String): DeleteResponse

    @GET("/api/movie-rules")
    suspend fun getMovieRulesList(): MovieRulesListResponse

    @GET("/api/movie-rules/{name}")
    suspend fun getMovieRule(@Path("name") name: String): MovieRuleResponse

    @POST("/api/movie-rules")
    suspend fun createMovieRule(@Body body: MovieRuleRequest): MovieRuleResponse

    @PUT("/api/movie-rules/{name}")
    suspend fun updateMovieRule(@Path("name") name: String, @Body body: MovieRuleRequest): MovieRuleResponse

    @DELETE("/api/movie-rules/{name}")
    suspend fun deleteMovieRule(@Path("name") name: String): DeleteResponse

    @GET("/api/series-list")
    suspend fun getSeriesList(): SeriesListResponse

    @POST("/api/rules/assign")
    suspend fun assignSeriesRule(@Body body: AssignSeriesRuleRequest): AssignSeriesRuleResponse

    @GET("/api/radarr/movies")
    suspend fun getRadarrMovies(): RadarrMoviesResponse

    @POST("/api/movie-rules/assign")
    suspend fun assignMovieRule(@Body body: AssignMovieRuleRequest): AssignMovieRuleResponse

    @GET("/api/setup-schema")
    suspend fun getSetupSchema(): SetupSchemaResponse

    @POST("/api/toggle-service/{service}")
    suspend fun toggleService(@Path("service") service: String, @Body body: ToggleServiceRequest): ToggleServiceResponse

    @POST("/api/save-service/{service}")
    suspend fun saveService(@Path("service") service: String, @Body body: JsonObject): SaveServiceResponse

    @POST("/api/test-connection/{service}")
    suspend fun testConnection(@Path("service") service: String, @Body body: JsonObject): SaveServiceResponse

    @GET("/api/logs")
    suspend fun getLogs(
        @Query("log_file") logFile: String,
        @Query("lines") lines: Int,
        @Query("level") level: String,
        @Query("search") search: String
    ): LogsResponse

    @GET("/api/global-settings")
    suspend fun getGlobalSettings(): GlobalSettingsResponse

    @POST("/api/global-settings")
    suspend fun updateGlobalSettings(@Body body: GlobalSettings): GlobalSettingsResponse

    @GET("/api/pending-deletions")
    suspend fun getPendingDeletions(): PendingDeletionsResponse

    @POST("/pending-deletions/approve")
    suspend fun approveEpisodeDeletions(@Body body: EpisodeIdsRequest): ApproveResult

    @POST("/pending-deletions/reject")
    suspend fun rejectEpisodeDeletions(@Body body: EpisodeIdsRequest): RejectResult

    @POST("/pending-deletions/movies/approve")
    suspend fun approveMovieDeletions(@Body body: MovieIdsRequest): ApproveResult

    @POST("/pending-deletions/movies/reject")
    suspend fun rejectMovieDeletions(@Body body: MovieIdsRequest): RejectResult

    @GET("/api/pending-watch-events")
    suspend fun getPendingWatchEvents(): PendingWatchEventsResponse

    @POST("/api/pending-watch-events/{itemId}/process")
    suspend fun processPendingWatchEvent(@Path("itemId") itemId: String): ProcessWatchEventResult

    @POST("/api/pending-watch-events/{itemId}/clear")
    suspend fun clearPendingWatchEvent(@Path("itemId") itemId: String): SimpleSuccessResponse

    @POST("/api/pending-watch-events/clear-all")
    suspend fun clearAllPendingWatchEvents(): SimpleSuccessResponse

    @GET("/api/pending-requests")
    suspend fun getPendingRequests(): PendingRequestsResponse

    @POST("/api/pending-requests/apply-rule")
    suspend fun applyRuleToSelection(@Body body: ApplySelectionRuleRequest): ApplySelectionRuleResult

    @POST("/api/delete-request/{requestId}")
    suspend fun deletePendingRequest(@Path("requestId") requestId: String): SaveServiceResponse
}
