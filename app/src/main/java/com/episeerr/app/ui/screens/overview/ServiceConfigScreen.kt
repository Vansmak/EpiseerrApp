package com.episeerr.app.ui.screens.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.episeerr.app.ui.components.FormSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceConfigScreen(
    viewModel: ServiceConfigViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (uiState.connected) "Enabled" else "Not configured yet",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = uiState.enabled,
                    onCheckedChange = viewModel::toggleEnabled,
                    enabled = uiState.connected
                )
            }
            FormSpacer()

            if (uiState.fields.isEmpty()) {
                Text(
                    "No configurable fields for this service.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            uiState.fields.forEach { field ->
                val value = uiState.values[field.name] ?: ""
                OutlinedTextField(
                    value = value,
                    onValueChange = { viewModel.updateField(field.name, it) },
                    label = { Text(field.label) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (field.type) {
                            "number" -> KeyboardType.Number
                            "url" -> KeyboardType.Uri
                            else -> KeyboardType.Text
                        }
                    ),
                    visualTransformation = if (field.type == "password") {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                field.help?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                }
            }

            FormSpacer()

            uiState.testResult?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                FormSpacer()
            }
            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                FormSpacer()
            }

            OutlinedButton(
                onClick = viewModel::testConnection,
                enabled = !uiState.isTesting && uiState.fields.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isTesting) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text("Test Connection")
                }
            }
            FormSpacer()

            Button(
                onClick = viewModel::save,
                enabled = !uiState.isSaving && uiState.fields.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text("Save")
                }
            }
        }
    }
}
