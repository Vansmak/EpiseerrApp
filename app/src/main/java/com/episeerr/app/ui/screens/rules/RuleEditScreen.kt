package com.episeerr.app.ui.screens.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleEditScreen(
    viewModel: RuleEditViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saved, uiState.deleted) {
        if (uiState.saved || uiState.deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Rule" else "New Rule") },
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
                Spacer()
            }

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.update { copy(description = it) } },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer()

            SectionLabel("Get episodes")
            TwoChoiceRow(
                selected = uiState.getType,
                options = "episodes" to "Episodes" to ("all" to "All"),
                onSelect = { viewModel.update { copy(getType = it) } }
            )
            if (uiState.getType == "episodes") {
                NumberField("Get count", uiState.getCount) { viewModel.update { copy(getCount = it) } }
            }
            Spacer()

            SectionLabel("Keep episodes")
            TwoChoiceRow(
                selected = uiState.keepType,
                options = "episodes" to "Episodes" to ("all" to "All"),
                onSelect = { viewModel.update { copy(keepType = it) } }
            )
            if (uiState.keepType == "episodes") {
                NumberField("Keep count", uiState.keepCount) { viewModel.update { copy(keepCount = it) } }
            }
            Spacer()

            SectionLabel("Action on new episodes")
            TwoChoiceRow(
                selected = uiState.actionOption,
                options = "monitor" to "Monitor" to ("search" to "Search"),
                onSelect = { viewModel.update { copy(actionOption = it) } }
            )
            Spacer()

            SwitchRow("Keep monitored if watched", uiState.monitorWatched) {
                viewModel.update { copy(monitorWatched = it) }
            }
            NumberField("Grace period - watched (days)", uiState.graceWatched, optional = true) {
                viewModel.update { copy(graceWatched = it) }
            }
            NumberField("Grace period - unwatched (days)", uiState.graceUnwatched, optional = true) {
                viewModel.update { copy(graceUnwatched = it) }
            }
            NumberField("Dormant days", uiState.dormantDays, optional = true) {
                viewModel.update { copy(dormantDays = it) }
            }
            Spacer()

            SectionLabel("Grace scope")
            TwoChoiceRow(
                selected = uiState.graceScope,
                options = "series" to "Series" to ("season" to "Season"),
                onSelect = { viewModel.update { copy(graceScope = it) } }
            )
            Spacer()

            SwitchRow("Keep pilot episode (S1E1) permanently", uiState.keepPilot) {
                viewModel.update { copy(keepPilot = it) }
            }
            SwitchRow("Release keep on season finale", uiState.releaseKeepOnFinale) {
                viewModel.update { copy(releaseKeepOnFinale = it) }
            }
            SwitchRow("Unmonitor on series ended", uiState.unmonitorOnSeriesEnded) {
                viewModel.update { copy(unmonitorOnSeriesEnded = it) }
            }
            Spacer()

            OutlinedTextField(
                value = uiState.alwaysHave,
                onValueChange = { viewModel.update { copy(alwaysHave = it) } },
                label = { Text("Always have (e.g. s1, s*e1+)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer()

            SwitchRow("Dry run (log only, don't delete)", uiState.dryRun) {
                viewModel.update { copy(dryRun = it) }
            }
            SwitchRow("Set as default rule", uiState.setAsDefault) {
                viewModel.update { copy(setAsDefault = it) }
            }

            uiState.error?.let {
                Spacer()
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer()
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
            Spacer()
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete rule?") },
            text = { Text("This removes '${uiState.ruleName}' and cleans up its Sonarr tag. This can't be undone.") },
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

@Composable
private fun Spacer() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(6.dp))
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
}

@Composable
private fun TwoChoiceRow(
    selected: String,
    options: Pair<Pair<String, String>, Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    val (first, second) = options
    Row {
        FilterChip(
            selected = selected == first.first,
            onClick = { onSelect(first.first) },
            label = { Text(first.second) },
            modifier = Modifier.padding(end = 8.dp)
        )
        FilterChip(
            selected = selected == second.first,
            onClick = { onSelect(second.first) },
            label = { Text(second.second) }
        )
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun NumberField(label: String, value: String, optional: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { new -> if (new.all { it.isDigit() }) onValueChange(new) },
        label = { Text(if (optional) "$label (optional)" else label) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}
