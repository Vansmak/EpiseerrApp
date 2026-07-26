package com.episeerr.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

// --- Dashboard ---

@Serializable
data class DashboardStatsResponse(
    val success: Boolean = false,
    // Per-integration status map (sonarr/radarr/sabnzbd/jellyfin/...), heterogeneous per
    // integration - kept as raw JSON rather than one data class per integration since the
    // set of integrations (and their fields) varies per user/instance.
    val stats: JsonObject = JsonObject(emptyMap())
)

@Serializable
data class DashboardActivityResponse(
    val success: Boolean = false,
    val services: List<ActivityItem> = emptyList()
)

@Serializable
data class ActivityItem(
    val service: String? = null,
    val action: String? = null,
    val details: String? = null,
    val timestamp: String? = null
)

// --- Episode rules ---

@Serializable
data class RulesListResponse(
    val success: Boolean = false,
    val rules: List<RuleSummary> = emptyList(),
    @SerialName("total_count") val totalCount: Int? = null
)

@Serializable
data class RuleSummary(
    val name: String,
    @SerialName("display_name") val displayName: String? = null,
    val description: String? = null,
    @SerialName("series_count") val seriesCount: Int? = null,
    @SerialName("is_default") val isDefault: Boolean = false
)

@Serializable
data class RuleResponse(
    val success: Boolean = false,
    val rule: Rule? = null,
    val error: String? = null
)

@Serializable
data class Rule(
    val name: String? = null,
    val description: String = "",
    @SerialName("get_type") val getType: String = "episodes",
    @SerialName("get_count") val getCount: Int? = 1,
    @SerialName("keep_type") val keepType: String = "episodes",
    @SerialName("keep_count") val keepCount: Int? = 1,
    @SerialName("action_option") val actionOption: String = "monitor",
    @SerialName("monitor_watched") val monitorWatched: Boolean = false,
    @SerialName("grace_watched") val graceWatched: Int? = null,
    @SerialName("grace_unwatched") val graceUnwatched: Int? = null,
    @SerialName("dormant_days") val dormantDays: Int? = null,
    @SerialName("grace_scope") val graceScope: String = "series",
    @SerialName("keep_pilot") val keepPilot: Boolean = false,
    @SerialName("release_keep_on_finale") val releaseKeepOnFinale: Boolean = false,
    @SerialName("unmonitor_on_series_ended") val unmonitorOnSeriesEnded: Boolean = false,
    @SerialName("always_have") val alwaysHave: String = "",
    @SerialName("dry_run") val dryRun: Boolean = false,
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("series_count") val seriesCount: Int? = null
)

@Serializable
data class RuleRequest(
    @SerialName("rule_name") val ruleName: String? = null,
    val description: String = "",
    @SerialName("get_type") val getType: String = "episodes",
    @SerialName("get_count") val getCount: Int? = 1,
    @SerialName("keep_type") val keepType: String = "episodes",
    @SerialName("keep_count") val keepCount: Int? = 1,
    @SerialName("action_option") val actionOption: String = "monitor",
    @SerialName("monitor_watched") val monitorWatched: Boolean = false,
    @SerialName("grace_watched") val graceWatched: Int? = null,
    @SerialName("grace_unwatched") val graceUnwatched: Int? = null,
    @SerialName("dormant_days") val dormantDays: Int? = null,
    @SerialName("grace_scope") val graceScope: String = "series",
    @SerialName("keep_pilot") val keepPilot: Boolean = false,
    @SerialName("release_keep_on_finale") val releaseKeepOnFinale: Boolean = false,
    @SerialName("unmonitor_on_series_ended") val unmonitorOnSeriesEnded: Boolean = false,
    @SerialName("always_have") val alwaysHave: String = "",
    @SerialName("dry_run") val dryRun: Boolean = false,
    @SerialName("set_as_default") val setAsDefault: Boolean = false
)

// --- Movie rules ---

@Serializable
data class MovieRulesListResponse(
    val success: Boolean = false,
    val rules: List<MovieRuleSummary> = emptyList(),
    @SerialName("total_count") val totalCount: Int? = null
)

@Serializable
data class MovieRuleSummary(
    val name: String,
    @SerialName("display_name") val displayName: String? = null,
    val description: String? = null,
    @SerialName("movie_count") val movieCount: Int? = null,
    @SerialName("is_default") val isDefault: Boolean = false
)

@Serializable
data class MovieRuleResponse(
    val success: Boolean = false,
    val rule: MovieRule? = null,
    val error: String? = null
)

@Serializable
data class MovieRule(
    val name: String? = null,
    val description: String = "",
    @SerialName("grace_watched") val graceWatched: Int? = null,
    @SerialName("dormant_days") val dormantDays: Int? = null,
    @SerialName("require_approval") val requireApproval: Boolean = false,
    @SerialName("dry_run") val dryRun: Boolean = false,
    @SerialName("delete_option") val deleteOption: String = "file_only",
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("movie_count") val movieCount: Int? = null
)

@Serializable
data class MovieRuleRequest(
    @SerialName("rule_name") val ruleName: String? = null,
    @SerialName("new_name") val newName: String? = null,
    val description: String = "",
    @SerialName("grace_watched") val graceWatched: Int? = null,
    @SerialName("dormant_days") val dormantDays: Int? = null,
    @SerialName("require_approval") val requireApproval: Boolean = false,
    @SerialName("dry_run") val dryRun: Boolean = false,
    @SerialName("delete_option") val deleteOption: String = "file_only",
    @SerialName("set_as_default") val setAsDefault: Boolean = false
)

// --- Sonarr series browser ---

@Serializable
data class SeriesListResponse(
    val success: Boolean = false,
    val series: List<SonarrSeries> = emptyList(),
    val error: String? = null
)

@Serializable
data class SonarrSeries(
    val id: Int,
    val title: String = "",
    val year: Int? = null,
    val monitored: Boolean = false,
    val poster: String? = null,
    @SerialName("assigned_rule") val assignedRule: String? = null
)

@Serializable
data class AssignSeriesRuleRequest(
    @SerialName("series_id") val seriesId: Int,
    @SerialName("rule_name") val ruleName: String
)

@Serializable
data class AssignSeriesRuleResponse(
    val success: Boolean = false,
    @SerialName("assigned_rule") val assignedRule: String? = null,
    val error: String? = null
)

// --- Radarr movie browser ---

@Serializable
data class RadarrMoviesResponse(
    val success: Boolean = false,
    val movies: List<RadarrMovie> = emptyList(),
    val error: String? = null
)

@Serializable
data class RadarrMovie(
    val id: Int,
    val title: String = "",
    val year: Int? = null,
    @SerialName("hasFile") val hasFile: Boolean = false,
    val monitored: Boolean = false,
    val poster: String? = null,
    @SerialName("assigned_rule") val assignedRule: String? = null
)

@Serializable
data class AssignMovieRuleRequest(
    @SerialName("movie_id") val movieId: Int,
    @SerialName("rule_name") val ruleName: String
)

@Serializable
data class AssignMovieRuleResponse(
    val success: Boolean = false,
    @SerialName("assigned_rule") val assignedRule: String? = null,
    val error: String? = null
)

@Serializable
data class DeleteResponse(
    val success: Boolean = false,
    val error: String? = null
)

// --- Global settings ---

@Serializable
data class GlobalSettingsResponse(
    val status: String = "",
    val settings: GlobalSettings? = null,
    @SerialName("disk_info") val diskInfo: JsonElement? = null
)

@Serializable
data class GlobalSettings(
    @SerialName("global_storage_min_gb") val globalStorageMinGb: Int? = null,
    @SerialName("cleanup_interval_hours") val cleanupIntervalHours: Int = 6,
    @SerialName("dry_run_mode") val dryRunMode: Boolean = false,
    @SerialName("auto_assign_new_series") val autoAssignNewSeries: Boolean = false,
    @SerialName("notifications_enabled") val notificationsEnabled: Boolean = false,
    @SerialName("discord_webhook_url") val discordWebhookUrl: String = "",
    @SerialName("notify_aired_not_downloaded") val notifyAiredNotDownloaded: Boolean = false,
    @SerialName("automation_held") val automationHeld: Boolean = false,
    @SerialName("reconcile_enabled") val reconcileEnabled: Boolean = false
)

// --- Setup / services ---

@Serializable
data class SetupSchemaResponse(
    val success: Boolean = false,
    @SerialName("setup_complete") val setupComplete: Boolean = false,
    val sonarr: SonarrSetupInfo? = null,
    val tmdb: ServiceSetupInfo? = null,
    val integrations: Map<String, IntegrationSetupInfo> = emptyMap()
)

@Serializable
data class SonarrSetupInfo(
    val enabled: Boolean = false,
    val connected: Boolean = false,
    val url: String? = null,
    val apikey: String? = null,
    @SerialName("alternate_url") val alternateUrl: String = "",
    @SerialName("open_in_iframe") val openInIframe: Boolean = false,
    @SerialName("default_quality_profile_id") val defaultQualityProfileId: String = ""
)

@Serializable
data class ServiceSetupInfo(
    val enabled: Boolean = false,
    val connected: Boolean = false,
    val apikey: String? = null
)

@Serializable
data class IntegrationSetupInfo(
    @SerialName("service_name") val serviceName: String,
    @SerialName("display_name") val displayName: String,
    val connected: Boolean = false,
    val enabled: Boolean = false,
    val apikey: String? = null,
    @SerialName("setup_fields") val setupFields: List<SetupField> = emptyList(),
    @SerialName("saved_values") val savedValues: Map<String, JsonElement> = emptyMap()
)

@Serializable
data class SetupField(
    val name: String,
    val label: String? = null,
    val type: String = "text",
    val placeholder: String? = null,
    val required: Boolean = false,
    val help: String? = null,
    @SerialName("help_text") val helpText: String? = null
)

@Serializable
data class SaveServiceResponse(
    val status: String = "",
    val message: String = ""
)

// --- Pending deletions ---

@Serializable
data class PendingDeletionsResponse(
    val success: Boolean = false,
    val episodes: PendingEpisodesSummary = PendingEpisodesSummary(),
    val movies: PendingMoviesSummary = PendingMoviesSummary(),
    val error: String? = null
)

@Serializable
data class PendingEpisodesSummary(
    @SerialName("total_series") val totalSeries: Int = 0,
    @SerialName("total_episodes") val totalEpisodes: Int = 0,
    @SerialName("total_size_gb") val totalSizeGb: Double = 0.0,
    val series: List<PendingSeriesGroup> = emptyList()
)

@Serializable
data class PendingSeriesGroup(
    @SerialName("series_id") val seriesId: Int,
    @SerialName("series_title") val seriesTitle: String,
    val seasons: List<PendingSeasonGroup> = emptyList()
)

@Serializable
data class PendingSeasonGroup(
    @SerialName("season_number") val seasonNumber: Int,
    val episodes: List<PendingEpisode> = emptyList()
)

@Serializable
data class PendingEpisode(
    @SerialName("episode_id") val episodeId: Int,
    @SerialName("episode_number") val episodeNumber: Int,
    val title: String = "",
    val reason: String = "",
    @SerialName("rule_name") val ruleName: String = "",
    @SerialName("file_size_mb") val fileSizeMb: Double = 0.0,
    @SerialName("queued_at") val queuedAt: String = ""
)

@Serializable
data class PendingMoviesSummary(
    @SerialName("total_movies") val totalMovies: Int = 0,
    @SerialName("total_size_gb") val totalSizeGb: Double = 0.0,
    val movies: List<PendingMovie> = emptyList()
)

@Serializable
data class PendingMovie(
    @SerialName("movie_id") val movieId: Int,
    @SerialName("movie_title") val movieTitle: String = "",
    @SerialName("file_size_mb") val fileSizeMb: Double = 0.0,
    @SerialName("rule_name") val ruleName: String = "",
    val reason: String = "",
    @SerialName("queued_at") val queuedAt: String = ""
)

@Serializable
data class EpisodeIdsRequest(@SerialName("episode_ids") val episodeIds: List<Int>)

@Serializable
data class MovieIdsRequest(@SerialName("movie_ids") val movieIds: List<Int>)

@Serializable
data class ApproveResult(
    val success: Boolean = false,
    @SerialName("deleted_count") val deletedCount: Int = 0,
    val errors: List<String> = emptyList(),
    val error: String? = null
)

@Serializable
data class RejectResult(
    val success: Boolean = false,
    @SerialName("rejected_count") val rejectedCount: Int = 0,
    val error: String? = null
)

@Serializable
data class SimpleSuccessResponse(val success: Boolean = false, val error: String? = null)

// --- Pending watch events ---

@Serializable
data class PendingWatchEventsResponse(
    val success: Boolean = false,
    val count: Int = 0,
    val items: List<PendingWatchEventItem> = emptyList(),
    @SerialName("last_checked") val lastChecked: String? = null,
    val error: String? = null
)

@Serializable
data class PendingWatchEventItem(
    val id: String,
    @SerialName("series_id") val seriesId: Int,
    @SerialName("series_title") val seriesTitle: String = "",
    val season: Int,
    val episode: Int,
    val source: String = "",
    val user: String = ""
)

@Serializable
data class ProcessWatchEventResult(
    val success: Boolean = false,
    val processed: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

// --- Logs ---

@Serializable
data class LogsResponse(
    val success: Boolean = false,
    @SerialName("log_file") val logFile: String = "",
    @SerialName("log_lines") val logLines: List<String> = emptyList(),
    @SerialName("total_lines") val totalLines: Int = 0,
    @SerialName("log_size") val logSize: String = "",
    @SerialName("available_logs") val availableLogs: List<String> = emptyList(),
    val error: String? = null
)

@Serializable
data class ToggleServiceRequest(val enabled: Boolean)

@Serializable
data class ToggleServiceResponse(
    val ok: Boolean = false,
    val service: String? = null,
    val enabled: Boolean = false,
    val error: String? = null
)
