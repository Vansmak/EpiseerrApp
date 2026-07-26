package com.episeerr.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.episeerr.app.ui.components.FormSpacer
import com.episeerr.app.ui.components.NumberField
import com.episeerr.app.ui.components.SectionLabel
import com.episeerr.app.ui.components.SwitchRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSettingsScreen(viewModel: GlobalSettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
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
            SectionLabel("Storage gate")
            NumberField(
                "Minimum free space (GB)",
                uiState.storageMinGb,
                optional = true
            ) { viewModel.update { copy(storageMinGb = it) } }
            NumberField("Cleanup interval (hours)", uiState.cleanupIntervalHours) {
                viewModel.update { copy(cleanupIntervalHours = it) }
            }
            FormSpacer()

            SwitchRow("Dry run mode (log only, don't delete)", uiState.dryRunMode) {
                viewModel.update { copy(dryRunMode = it) }
            }
            SwitchRow("Auto-assign new series to default rule", uiState.autoAssignNewSeries) {
                viewModel.update { copy(autoAssignNewSeries = it) }
            }
            SwitchRow("Hold automation (vacation mode)", uiState.automationHeld) {
                viewModel.update { copy(automationHeld = it) }
            }
            SwitchRow("Reconcile missed watch events", uiState.reconcileEnabled) {
                viewModel.update { copy(reconcileEnabled = it) }
            }
            FormSpacer()

            SectionLabel("Notifications")
            SwitchRow("Notifications enabled", uiState.notificationsEnabled) {
                viewModel.update { copy(notificationsEnabled = it) }
            }

            uiState.error?.let {
                FormSpacer()
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            if (uiState.saved) {
                FormSpacer()
                Text("Saved", color = MaterialTheme.colorScheme.primary)
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
                    Text("Save")
                }
            }

            FormSpacer()
            FormSpacer()
            OutlinedButton(onClick = viewModel::disconnectServer, modifier = Modifier.fillMaxWidth()) {
                Text("Disconnect from this server")
            }
        }
    }
}
