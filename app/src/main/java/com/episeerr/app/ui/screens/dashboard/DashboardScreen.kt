package com.episeerr.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.episeerr.app.data.model.ActivityItem
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(padding)
            uiState.error != null -> ErrorState(padding, uiState.error!!, viewModel::refresh)
            else -> DashboardContent(padding, uiState.stats, uiState.activity)
        }
    }
}

@Composable
private fun LoadingState(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(padding: PaddingValues, message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Couldn't load dashboard", style = MaterialTheme.typography.titleMedium)
        Text(message, style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}

/** Pulls out scalar (string/number/bool) fields from an integration's status object as display lines. */
private fun JsonObject.scalarSummaryLines(): List<String> =
    entries.mapNotNull { (key, value) ->
        val primitive = value as? JsonPrimitive ?: return@mapNotNull null
        if (key == "configured") return@mapNotNull null
        "$key: ${primitive.content}"
    }

@Composable
private fun DashboardContent(padding: PaddingValues, stats: JsonObject, activity: List<ActivityItem>) {
    val services = stats.entries
        .mapNotNull { (name, value) -> (value as? JsonObject)?.let { name to it } }
        .sortedByDescending { (_, obj) -> (obj["configured"] as? JsonPrimitive)?.booleanOrNull ?: true }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, padding.calculateTopPadding() + 8.dp, 16.dp, 16.dp)
    ) {
        items(services) { (name, obj) ->
            val configured = (obj["configured"] as? JsonPrimitive)?.booleanOrNull ?: true
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(name.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
                    if (!configured) {
                        Text("Not configured", style = MaterialTheme.typography.bodySmall)
                    } else {
                        obj.scalarSummaryLines().forEach { line ->
                            Text(line, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        item {
            Text(
                "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )
        }
        if (activity.isEmpty()) {
            item { Text("Nothing recent.", style = MaterialTheme.typography.bodySmall) }
        } else {
            items(activity) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "${item.service ?: ""} - ${item.action ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        item.details?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}
