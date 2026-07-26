package com.episeerr.app.data

import com.episeerr.app.data.api.EpiseerrApi
import com.episeerr.app.data.model.DashboardActivityResponse
import com.episeerr.app.data.model.DashboardStatsResponse
import com.episeerr.app.data.model.DeleteResponse
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

    suspend fun getSetupSchema(): ApiResult<SetupSchemaResponse> = apiCall { api.getSetupSchema() }

    suspend fun toggleService(service: String, enabled: Boolean): ApiResult<ToggleServiceResponse> =
        apiCall { api.toggleService(service, ToggleServiceRequest(enabled)) }

    suspend fun getGlobalSettings(): ApiResult<GlobalSettingsResponse> = apiCall { api.getGlobalSettings() }

    suspend fun updateGlobalSettings(settings: GlobalSettings): ApiResult<GlobalSettingsResponse> =
        apiCall { api.updateGlobalSettings(settings) }
}
