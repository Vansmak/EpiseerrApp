package com.episeerr.app.ui.screens.rules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.model.Rule
import com.episeerr.app.data.model.RuleRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RuleFormState(
    val ruleName: String = "",
    val isEditMode: Boolean = false,
    val description: String = "",
    val getType: String = "episodes",
    val getCount: String = "1",
    val keepType: String = "episodes",
    val keepCount: String = "1",
    val actionOption: String = "monitor",
    val monitorWatched: Boolean = false,
    val graceWatched: String = "",
    val graceUnwatched: String = "",
    val dormantDays: String = "",
    val graceScope: String = "series",
    val keepPilot: Boolean = false,
    val releaseKeepOnFinale: Boolean = false,
    val unmonitorOnSeriesEnded: Boolean = false,
    val alwaysHave: String = "",
    val dryRun: Boolean = false,
    val setAsDefault: Boolean = false,
    val seriesCount: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
    val deleted: Boolean = false
)

@HiltViewModel
class RuleEditViewModel @Inject constructor(
    private val repository: EpiseerrRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val existingRuleName: String? = savedStateHandle.get<String>("ruleName")

    private val _uiState = MutableStateFlow(
        RuleFormState(
            ruleName = existingRuleName.orEmpty(),
            isEditMode = existingRuleName != null,
            isLoading = existingRuleName != null
        )
    )
    val uiState: StateFlow<RuleFormState> = _uiState.asStateFlow()

    init {
        existingRuleName?.let(::loadRule)
    }

    private fun loadRule(name: String) {
        viewModelScope.launch {
            when (val result = repository.getRule(name)) {
                is ApiResult.Success -> {
                    val rule = result.data.rule
                    if (rule != null) {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null).applyRule(rule)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = result.data.error ?: "Rule not found")
                    }
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    private fun RuleFormState.applyRule(rule: Rule) = copy(
        description = rule.description,
        getType = rule.getType,
        getCount = rule.getCount?.toString() ?: "1",
        keepType = rule.keepType,
        keepCount = rule.keepCount?.toString() ?: "1",
        actionOption = rule.actionOption,
        monitorWatched = rule.monitorWatched,
        graceWatched = rule.graceWatched?.toString() ?: "",
        graceUnwatched = rule.graceUnwatched?.toString() ?: "",
        dormantDays = rule.dormantDays?.toString() ?: "",
        graceScope = rule.graceScope,
        keepPilot = rule.keepPilot,
        releaseKeepOnFinale = rule.releaseKeepOnFinale,
        unmonitorOnSeriesEnded = rule.unmonitorOnSeriesEnded,
        alwaysHave = rule.alwaysHave,
        seriesCount = rule.seriesCount ?: 0,
        dryRun = rule.dryRun,
        setAsDefault = rule.isDefault
    )

    fun update(transform: RuleFormState.() -> RuleFormState) {
        _uiState.value = _uiState.value.transform().copy(error = null)
    }

    fun save() {
        val state = _uiState.value
        if (!state.isEditMode && state.ruleName.isBlank()) {
            _uiState.value = state.copy(error = "Rule name is required")
            return
        }

        val request = RuleRequest(
            ruleName = if (state.isEditMode) null else state.ruleName.trim(),
            description = state.description,
            getType = state.getType,
            getCount = if (state.getType == "all") null else state.getCount.toIntOrNull() ?: 1,
            keepType = state.keepType,
            keepCount = if (state.keepType == "all") null else state.keepCount.toIntOrNull() ?: 1,
            actionOption = state.actionOption,
            monitorWatched = state.monitorWatched,
            graceWatched = state.graceWatched.toIntOrNull(),
            graceUnwatched = state.graceUnwatched.toIntOrNull(),
            dormantDays = state.dormantDays.toIntOrNull(),
            graceScope = state.graceScope,
            keepPilot = state.keepPilot,
            releaseKeepOnFinale = state.releaseKeepOnFinale,
            unmonitorOnSeriesEnded = state.unmonitorOnSeriesEnded,
            alwaysHave = state.alwaysHave,
            dryRun = state.dryRun,
            setAsDefault = state.setAsDefault
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val result = if (state.isEditMode) {
                repository.updateRule(state.ruleName, request)
            } else {
                repository.createRule(request)
            }
            when (result) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = result.data.error ?: "Save failed"
                        )
                    }
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isSaving = false, error = result.message)
            }
        }
    }

    fun delete() {
        val name = _uiState.value.ruleName
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            when (val result = repository.deleteRule(name)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isSaving = false, deleted = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isSaving = false, error = result.message)
            }
        }
    }
}
