package com.episeerr.app.ui.screens.rules

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.episeerr.app.data.model.RuleSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesListScreen(
    viewModel: RulesListViewModel = hiltViewModel(),
    onOpenRule: (String) -> Unit,
    onCreateRule: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rules") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRule) {
                Icon(Icons.Filled.Add, contentDescription = "Create rule")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator() }

            uiState.error != null -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Couldn't load rules", style = MaterialTheme.typography.titleMedium)
                Text(uiState.error ?: "", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = viewModel::refresh) { Text("Retry") }
            }

            else -> RuleList(padding, uiState.rules, onOpenRule)
        }
    }
}

@Composable
private fun RuleList(padding: PaddingValues, rules: List<RuleSummary>, onOpenRule: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, padding.calculateTopPadding() + 8.dp, 16.dp, 88.dp)
    ) {
        items(rules) { rule ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { onOpenRule(rule.name) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(rule.displayName ?: rule.name, style = MaterialTheme.typography.titleMedium)
                        if (rule.isDefault) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Default rule",
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                    rule.description?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "${rule.seriesCount ?: 0} series",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        if (rules.isEmpty()) {
            item { Text("No rules yet - tap + to create one.", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
