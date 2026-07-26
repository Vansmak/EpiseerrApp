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
    val apikey: String? = null
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
