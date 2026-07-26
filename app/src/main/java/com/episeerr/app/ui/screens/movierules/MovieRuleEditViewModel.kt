package com.episeerr.app.ui.screens.movierules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.episeerr.app.data.ApiResult
import com.episeerr.app.data.EpiseerrRepository
import com.episeerr.app.data.model.MovieRule
import com.episeerr.app.data.model.MovieRuleRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieRuleFormState(
    val ruleName: String = "",
    val isEditMode: Boolean = false,
    val description: String = "",
    val graceWatched: String = "",
    val dormantDays: String = "",
    val requireApproval: Boolean = false,
    val dryRun: Boolean = false,
    val deleteOption: String = "file_only",
    val setAsDefault: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
    val deleted: Boolean = false
)

@HiltViewModel
class MovieRuleEditViewModel @Inject constructor(
    private val repository: EpiseerrRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val existingRuleName: String? = savedStateHandle.get<String>("ruleName")

    private val _uiState = MutableStateFlow(
        MovieRuleFormState(
            ruleName = existingRuleName.orEmpty(),
            isEditMode = existingRuleName != null,
            isLoading = existingRuleName != null
        )
    )
    val uiState: StateFlow<MovieRuleFormState> = _uiState.asStateFlow()

    init {
        existingRuleName?.let(::loadRule)
    }

    private fun loadRule(name: String) {
        viewModelScope.launch {
            when (val result = repository.getMovieRule(name)) {
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

    private fun MovieRuleFormState.applyRule(rule: MovieRule) = copy(
        description = rule.description,
        graceWatched = rule.graceWatched?.toString() ?: "",
        dormantDays = rule.dormantDays?.toString() ?: "",
        requireApproval = rule.requireApproval,
        dryRun = rule.dryRun,
        deleteOption = rule.deleteOption,
        setAsDefault = rule.isDefault
    )

    fun update(transform: MovieRuleFormState.() -> MovieRuleFormState) {
        _uiState.value = _uiState.value.transform().copy(error = null)
    }

    fun save() {
        val state = _uiState.value
        if (!state.isEditMode && state.ruleName.isBlank()) {
            _uiState.value = state.copy(error = "Rule name is required")
            return
        }

        val request = MovieRuleRequest(
            ruleName = if (state.isEditMode) null else state.ruleName.trim(),
            description = state.description,
            graceWatched = state.graceWatched.toIntOrNull(),
            dormantDays = state.dormantDays.toIntOrNull(),
            requireApproval = state.requireApproval,
            dryRun = state.dryRun,
            deleteOption = state.deleteOption,
            setAsDefault = state.setAsDefault
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val result = if (state.isEditMode) {
                repository.updateMovieRule(state.ruleName, request)
            } else {
                repository.createMovieRule(request)
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
            when (val result = repository.deleteMovieRule(name)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isSaving = false, deleted = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isSaving = false, error = result.message)
            }
        }
    }
}
