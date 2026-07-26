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
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.episeerr.app.data.model.MovieRuleSummary
import com.episeerr.app.data.model.RuleSummary
import com.episeerr.app.ui.screens.movierules.MovieRulesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesHomeScreen(
    episodeViewModel: RulesListViewModel = hiltViewModel(),
    movieViewModel: MovieRulesListViewModel = hiltViewModel(),
    onOpenRule: (String) -> Unit,
    onCreateRule: () -> Unit,
    onOpenMovieRule: (String) -> Unit,
    onCreateMovieRule: () -> Unit,
    onBrowseMovies: () -> Unit,
    onBrowseSeries: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val episodeState by episodeViewModel.uiState.collectAsState()
    val movieState by movieViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rules") },
                actions = {
                    if (tab == 0) {
                        IconButton(onClick = onBrowseSeries) {
                            Icon(Icons.Filled.Tv, contentDescription = "Browse series")
                        }
                    } else {
                        IconButton(onClick = onBrowseMovies) {
                            Icon(Icons.Filled.Movie, contentDescription = "Browse movies")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (tab == 0) onCreateRule() else onCreateMovieRule() }) {
                Icon(Icons.Filled.Add, contentDescription = "Create rule")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Episode") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Movie") })
            }

            when {
                tab == 0 && episodeState.isLoading -> LoadingBox()
                tab == 0 && episodeState.error != null -> ErrorBox(episodeState.error!!, episodeViewModel::refresh)
                tab == 0 -> EpisodeRuleList(episodeState.rules, onOpenRule)

                tab == 1 && movieState.isLoading -> LoadingBox()
                tab == 1 && movieState.error != null -> ErrorBox(movieState.error!!, movieViewModel::refresh)
                else -> MovieRuleList(movieState.rules, onOpenMovieRule)
            }
        }
    }
}

@Composable
private fun LoadingBox() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { CircularProgressIndicator() }
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Couldn't load rules", style = MaterialTheme.typography.titleMedium)
        Text(message, style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EpisodeRuleList(rules: List<RuleSummary>, onOpenRule: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp)
    ) {
        items(rules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onOpenRule(rule.name) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(rule.displayName ?: rule.name, style = MaterialTheme.typography.titleMedium)
                        if (rule.isDefault) {
                            Icon(Icons.Filled.Star, contentDescription = "Default rule", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                    rule.description?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    Text("${rule.seriesCount ?: 0} series", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (rules.isEmpty()) {
            item { Text("No rules yet - tap + to create one.", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun MovieRuleList(rules: List<MovieRuleSummary>, onOpenRule: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp)
    ) {
        items(rules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onOpenRule(rule.name) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(rule.displayName ?: rule.name, style = MaterialTheme.typography.titleMedium)
                        if (rule.isDefault) {
                            Icon(Icons.Filled.Star, contentDescription = "Default rule", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                    rule.description?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    Text("${rule.movieCount ?: 0} movies", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (rules.isEmpty()) {
            item { Text("No movie rules yet - tap + to create one.", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
