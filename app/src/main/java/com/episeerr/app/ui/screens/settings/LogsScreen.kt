package com.episeerr.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: LogsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)) {
                uiState.availableLogs.forEach { logFile ->
                    FilterChip(
                        selected = uiState.logFile == logFile,
                        onClick = { viewModel.onLogFileChange(logFile) },
                        label = { Text(logFile) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.search,
                onValueChange = viewModel::onSearchChange,
                label = { Text("Search") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp, 16.dp, 8.dp)
            )

            if (uiState.logSize.isNotBlank()) {
                Text(
                    "${uiState.logSize} - ${uiState.totalLines} lines total, showing last ${uiState.lines.size}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
                )
            }

            when {
                uiState.isLoading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }

                uiState.error != null -> Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Couldn't load logs", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.error ?: "", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = viewModel::refresh) { Text("Retry") }
                }

                uiState.lines.isEmpty() -> Text(
                    "No log lines.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )

                else -> {
                    val filtered = if (uiState.search.isBlank()) {
                        uiState.lines
                    } else {
                        uiState.lines.filter { it.contains(uiState.search, ignoreCase = true) }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)
                    ) {
                        items(filtered.asReversed()) { line ->
                            Text(
                                line,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
