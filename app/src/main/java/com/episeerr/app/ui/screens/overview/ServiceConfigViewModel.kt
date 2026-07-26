package com.episeerr.app.ui.screens.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

data class ConfigFieldDef(
    val name: String,
    val label: String,
    val type: String = "text",
    val help: String? = null
)

data class ServiceConfigUiState(
    val isLoading: Boolean = true,
    val serviceKey: String = "",
    val displayName: String = "",
    val enabled: Boolean = false,
    val connected: Boolean = false,
    val fields: List<ConfigFieldDef> = emptyList(),
    val values: Map<String, String> = emptyMap(),
    val isSaving: Boolean = false,
    val isTesting: Boolean = false,
    val testResult: String? = null,
    val error: String? = null,
    val saved: Boolean = false
)

@HiltViewModel
class ServiceConfigViewModel @Inject constructor(
    private val repository: EpiseerrRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val serviceKey: String = requireNotNull(savedStateHandle.get<String>("serviceKey"))

    private val _uiState = MutableStateFlow(ServiceConfigUiState(serviceKey = serviceKey))
    val uiState: StateFlow<ServiceConfigUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.getSetupSchema()) {
                is ApiResult.Success -> {
                    val schema = result.data
                    val state = when (serviceKey) {
                        "sonarr" -> ServiceConfigUiState(
                            isLoading = false,
                            serviceKey = serviceKey,
                            displayName = "Sonarr",
                            enabled = schema.sonarr?.enabled ?: false,
                            connected = schema.sonarr?.connected ?: false,
                            fields = listOf(
                                ConfigFieldDef("url", "Server URL", "url"),
                                ConfigFieldDef("apikey", "API Key", "password"),
                                ConfigFieldDef("alternate_url", "Alternate URL", "url"),
                                ConfigFieldDef("default_quality_profile_id", "Default Quality Profile ID", "number")
                            ),
                            values = mapOf(
                                "url" to (schema.sonarr?.url ?: ""),
                                "apikey" to (schema.sonarr?.apikey ?: ""),
                                "alternate_url" to (schema.sonarr?.alternateUrl ?: ""),
                                "default_quality_profile_id" to (schema.sonarr?.defaultQualityProfileId ?: "")
                            )
                        )
                        "tmdb" -> ServiceConfigUiState(
                            isLoading = false,
                            serviceKey = serviceKey,
                            displayName = "TMDB",
                            enabled = schema.tmdb?.enabled ?: false,
                            connected = schema.tmdb?.connected ?: false,
                            fields = listOf(ConfigFieldDef("apikey", "API Key (Bearer Token)", "password")),
                            values = mapOf("apikey" to (schema.tmdb?.apikey ?: ""))
                        )
                        else -> {
                            val integration = schema.integrations[serviceKey]
                            ServiceConfigUiState(
                                isLoading = false,
                                serviceKey = serviceKey,
                                displayName = integration?.displayName ?: serviceKey,
                                enabled = integration?.enabled ?: false,
                                connected = integration?.connected ?: false,
                                fields = integration?.setupFields?.map {
                                    ConfigFieldDef(
                                        name = it.name,
                                        label = it.label ?: it.name,
                                        type = it.type,
                                        help = it.help ?: it.helpText
                                    )
                                } ?: emptyList(),
                                values = integration?.savedValues?.mapValues { (_, v) ->
                                    (v as? JsonPrimitive)?.content ?: ""
                                } ?: emptyMap()
                            )
                        }
                    }
                    _uiState.value = state
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun updateField(name: String, value: String) {
        _uiState.value = _uiState.value.copy(
            values = _uiState.value.values + (name to value),
            testResult = null,
            error = null
        )
    }

    fun toggleEnabled(enabled: Boolean) {
        val previous = _uiState.value.enabled
        _uiState.value = _uiState.value.copy(enabled = enabled)
        viewModelScope.launch {
            when (repository.toggleService(serviceKey, enabled)) {
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(enabled = previous)
                else -> {}
            }
        }
    }

    private fun buildFieldsBody(): JsonObject {
        val state = _uiState.value
        return buildJsonObject {
            state.values.forEach { (name, value) -> put("${state.serviceKey}-$name", value) }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTesting = true, testResult = null, error = null)
            when (val result = repository.testConnection(_uiState.value.serviceKey, buildFieldsBody())) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    testResult = result.data.message
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isTesting = false, error = result.message)
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            when (val result = repository.saveService(_uiState.value.serviceKey, buildFieldsBody())) {
                is ApiResult.Success -> {
                    if (result.data.status == "success") {
                        _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
                    } else {
                        _uiState.value = _uiState.value.copy(isSaving = false, error = result.data.message)
                    }
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isSaving = false, error = result.message)
            }
        }
    }
}
