package com.episeerr.app.data.api

import com.episeerr.app.data.model.DashboardActivityResponse
import com.episeerr.app.data.model.DashboardStatsResponse
import com.episeerr.app.data.model.DeleteResponse
import com.episeerr.app.data.model.GlobalSettings
import com.episeerr.app.data.model.GlobalSettingsResponse
import com.episeerr.app.data.model.MovieRuleRequest
import com.episeerr.app.data.model.MovieRuleResponse
import com.episeerr.app.data.model.RuleRequest
import com.episeerr.app.data.model.RuleResponse
import com.episeerr.app.data.model.RulesListResponse
import com.episeerr.app.data.model.SetupSchemaResponse
import com.episeerr.app.data.model.ToggleServiceRequest
import com.episeerr.app.data.model.ToggleServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("/api/movie-rules/{name}")
    suspend fun getMovieRule(@Path("name") name: String): MovieRuleResponse

    @POST("/api/movie-rules")
    suspend fun createMovieRule(@Body body: MovieRuleRequest): MovieRuleResponse

    @PUT("/api/movie-rules/{name}")
    suspend fun updateMovieRule(@Path("name") name: String, @Body body: MovieRuleRequest): MovieRuleResponse

    @DELETE("/api/movie-rules/{name}")
    suspend fun deleteMovieRule(@Path("name") name: String): DeleteResponse

    @GET("/api/setup-schema")
    suspend fun getSetupSchema(): SetupSchemaResponse

    @POST("/api/toggle-service/{service}")
    suspend fun toggleService(@Path("service") service: String, @Body body: ToggleServiceRequest): ToggleServiceResponse

    @GET("/api/global-settings")
    suspend fun getGlobalSettings(): GlobalSettingsResponse

    @POST("/api/global-settings")
    suspend fun updateGlobalSettings(@Body body: GlobalSettings): GlobalSettingsResponse
}
