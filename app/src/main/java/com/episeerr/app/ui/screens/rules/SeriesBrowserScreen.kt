package com.episeerr.app.ui.screens.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.episeerr.app.data.model.SonarrSeries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesBrowserScreen(
    viewModel: SeriesBrowserViewModel = hiltViewModel(),
    rulesViewModel: RulesListViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rulesState by rulesViewModel.uiState.collectAsState()
    var pickerSeries by remember { mutableStateOf<SonarrSeries?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.filterRuleName?.let { "Series in \"$it\"" } ?: "Series") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchChange,
                label = { Text("Search") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)
            )

            if (uiState.filterRuleName != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp, 16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show all series (to add more)", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = uiState.showAllSeries,
                        onCheckedChange = viewModel::onShowAllChange,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
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
                    Text("Couldn't load series", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.error ?: "", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = viewModel::refresh) { Text("Retry") }
                }

                else -> {
                    val filtered = uiState.series.filter {
                        (uiState.searchQuery.isBlank() || it.title.contains(uiState.searchQuery, ignoreCase = true)) &&
                            (uiState.showAllSeries || it.assignedRule == uiState.filterRuleName)
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)
                    ) {
                        items(filtered) { series ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { pickerSeries = series }
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = series.poster,
                                        contentDescription = series.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(48.dp, 72.dp)
                                    )
                                    Column(modifier = Modifier.padding(start = 12.dp).fillMaxWidth()) {
                                        Text(
                                            "${series.title}${series.year?.let { " ($it)" } ?: ""}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            series.assignedRule ?: "No rule assigned",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pickerSeries?.let { series ->
        AlertDialog(
            onDismissRequest = { pickerSeries = null },
            title = { Text("Assign rule to \"${series.title}\"") },
            text = {
                Column {
                    rulesState.rules.forEach { rule ->
                        TextButton(onClick = {
                            viewModel.assign(series.id, rule.name)
                            pickerSeries = null
                        }) { Text(rule.displayName ?: rule.name) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { pickerSeries = null }) { Text("Cancel") } }
        )
    }
}
