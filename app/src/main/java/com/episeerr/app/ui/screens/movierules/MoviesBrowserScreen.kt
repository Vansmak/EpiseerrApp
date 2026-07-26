package com.episeerr.app.ui.screens.movierules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import com.episeerr.app.data.model.RadarrMovie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesBrowserScreen(
    viewModel: MoviesBrowserViewModel = hiltViewModel(),
    movieRulesViewModel: MovieRulesListViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rulesState by movieRulesViewModel.uiState.collectAsState()
    var pickerMovie by remember { mutableStateOf<RadarrMovie?>(null) }
    var showBulkPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.selectMode) "${uiState.selectedIds.size} selected"
                        else uiState.filterRuleName?.let { "Movies in \"$it\"" } ?: "Movies"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = if (uiState.selectMode) viewModel::toggleSelectMode else onBack) {
                        Icon(
                            if (uiState.selectMode) Icons.Filled.Close else Icons.Filled.ArrowBack,
                            contentDescription = if (uiState.selectMode) "Cancel selection" else "Back"
                        )
                    }
                },
                actions = {
                    if (!uiState.selectMode) {
                        IconButton(onClick = viewModel::toggleSelectMode) {
                            Icon(Icons.Filled.Checklist, contentDescription = "Select multiple")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.selectMode && uiState.selectedIds.isNotEmpty()) {
                BottomAppBar {
                    Button(
                        onClick = { showBulkPicker = true },
                        enabled = !uiState.isBulkAssigning,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        if (uiState.isBulkAssigning) {
                            CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                        } else {
                            Text("Assign ${uiState.selectedIds.size} to a rule")
                        }
                    }
                }
            }
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
                    Text("Show all movies (to add more)", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = uiState.showAllMovies,
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
                    Text("Couldn't load movies", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.error ?: "", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = viewModel::refresh) { Text("Retry") }
                }

                else -> {
                    val filtered = uiState.movies.filter {
                        (uiState.searchQuery.isBlank() || it.title.contains(uiState.searchQuery, ignoreCase = true)) &&
                            (uiState.showAllMovies || it.assignedRule == uiState.filterRuleName)
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)
                    ) {
                        items(filtered) { movie ->
                            val selected = movie.id in uiState.selectedIds
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable {
                                        if (uiState.selectMode) {
                                            viewModel.toggleSelected(movie.id)
                                        } else {
                                            pickerMovie = movie
                                        }
                                    }
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (uiState.selectMode) {
                                        Checkbox(checked = selected, onCheckedChange = { viewModel.toggleSelected(movie.id) })
                                    }
                                    AsyncImage(
                                        model = movie.poster,
                                        contentDescription = movie.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(48.dp, 72.dp)
                                    )
                                    Column(modifier = Modifier.padding(start = 12.dp).fillMaxWidth()) {
                                        Text(
                                            "${movie.title}${movie.year?.let { " ($it)" } ?: ""}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            movie.assignedRule ?: "Unassigned",
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

    pickerMovie?.let { movie ->
        AlertDialog(
            onDismissRequest = { pickerMovie = null },
            title = { Text("Assign rule to \"${movie.title}\"") },
            text = {
                Column {
                    TextButton(onClick = {
                        viewModel.assign(movie.id, "")
                        pickerMovie = null
                    }) { Text("Unassigned") }
                    rulesState.rules.forEach { rule ->
                        TextButton(onClick = {
                            viewModel.assign(movie.id, rule.name)
                            pickerMovie = null
                        }) { Text(rule.displayName ?: rule.name) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { pickerMovie = null }) { Text("Cancel") } }
        )
    }

    if (showBulkPicker) {
        AlertDialog(
            onDismissRequest = { showBulkPicker = false },
            title = { Text("Assign ${uiState.selectedIds.size} movies to a rule") },
            text = {
                Column {
                    TextButton(onClick = {
                        viewModel.assignSelected("")
                        showBulkPicker = false
                    }) { Text("Unassigned") }
                    rulesState.rules.forEach { rule ->
                        TextButton(onClick = {
                            viewModel.assignSelected(rule.name)
                            showBulkPicker = false
                        }) { Text(rule.displayName ?: rule.name) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showBulkPicker = false }) { Text("Cancel") } }
        )
    }
}
