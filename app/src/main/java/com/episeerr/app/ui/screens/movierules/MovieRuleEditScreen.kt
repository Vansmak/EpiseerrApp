package com.episeerr.app.ui.screens.movierules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.episeerr.app.ui.components.FormSpacer
import com.episeerr.app.ui.components.NumberField
import com.episeerr.app.ui.components.SwitchRow
import com.episeerr.app.ui.components.TwoChoiceRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieRuleEditScreen(
    viewModel: MovieRuleEditViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onManageMovies: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saved, uiState.deleted) {
        if (uiState.saved || uiState.deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Movie Rule" else "New Movie Rule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete rule")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (!uiState.isEditMode) {
                OutlinedTextField(
                    value = uiState.ruleName,
                    onValueChange = { name -> viewModel.update { copy(ruleName = name) } },
                    label = { Text("Rule name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                FormSpacer()
            } else {
                OutlinedButton(
                    onClick = { onManageMovies(uiState.ruleName) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage assigned movies (${uiState.movieCount})")
                }
                FormSpacer()
            }

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.update { copy(description = it) } },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            FormSpacer()

            NumberField("Grace period - watched (days)", uiState.graceWatched, optional = true) {
                viewModel.update { copy(graceWatched = it) }
            }
            NumberField("Dormant days", uiState.dormantDays, optional = true) {
                viewModel.update { copy(dormantDays = it) }
            }
            FormSpacer()

            TwoChoiceRow(
                selected = uiState.deleteOption,
                options = "file_only" to "File only" to ("remove_from_radarr" to "Remove from Radarr"),
                onSelect = { viewModel.update { copy(deleteOption = it) } }
            )
            FormSpacer()

            SwitchRow("Require approval before deleting", uiState.requireApproval) {
                viewModel.update { copy(requireApproval = it) }
            }
            SwitchRow("Dry run (log only, don't delete)", uiState.dryRun) {
                viewModel.update { copy(dryRun = it) }
            }
            SwitchRow("Set as default rule", uiState.setAsDefault) {
                viewModel.update { copy(setAsDefault = it) }
            }

            uiState.error?.let {
                FormSpacer()
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            FormSpacer()
            Button(
                onClick = viewModel::save,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text(if (uiState.isEditMode) "Save" else "Create")
                }
            }
            FormSpacer()
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete movie rule?") },
            text = { Text("This removes '${uiState.ruleName}' and its Radarr tag from all movies. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
