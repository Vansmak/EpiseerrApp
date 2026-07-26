package com.episeerr.app.ui.screens.pending

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.episeerr.app.data.model.PendingWatchEventItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingScreen(viewModel: PendingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    val hasSelection = uiState.selectedEpisodeIds.isNotEmpty() || uiState.selectedMovieIds.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending") },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        bottomBar = {
            if (tab == 0 && hasSelection) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = viewModel::rejectSelected,
                            enabled = !uiState.isActing,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) { Text("Reject") }
                        Button(
                            onClick = viewModel::approveSelected,
                            enabled = !uiState.isActing,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isActing) {
                                CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                            } else {
                                Text("Approve")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Deletions") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Watch Events") })
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
                    Text("Couldn't load pending items", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.error ?: "", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = viewModel::refresh) { Text("Retry") }
                }

                tab == 0 -> DeletionsTab(uiState, viewModel)
                else -> WatchEventsTab(uiState, viewModel)
            }
        }
    }
}

@Composable
private fun DeletionsTab(uiState: PendingUiState, viewModel: PendingViewModel) {
    val totalCount = uiState.episodesSummary.totalEpisodes + uiState.moviesSummary.totalMovies
    if (totalCount == 0) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { Text("Nothing pending deletion.", style = MaterialTheme.typography.bodyMedium) }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp)
    ) {
        uiState.episodesSummary.series.forEach { series ->
            item(key = "series_${series.seriesId}") {
                Text(series.seriesTitle, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            }
            series.seasons.forEach { season ->
                items(season.episodes, key = { "ep_${it.episodeId}" }) { episode ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = episode.episodeId in uiState.selectedEpisodeIds,
                                onCheckedChange = { viewModel.toggleEpisodeSelected(episode.episodeId) }
                            )
                            Column {
                                Text(
                                    "S${season.seasonNumber}E${episode.episodeNumber} - ${episode.title}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "${episode.reason} - ${episode.ruleName} - ${episode.fileSizeMb} MB",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
        if (uiState.moviesSummary.movies.isNotEmpty()) {
            item(key = "movies_header") {
                Text("Movies", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            }
            items(uiState.moviesSummary.movies, key = { "movie_${it.movieId}" }) { movie ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = movie.movieId in uiState.selectedMovieIds,
                            onCheckedChange = { viewModel.toggleMovieSelected(movie.movieId) }
                        )
                        Column {
                            Text(movie.movieTitle, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${movie.reason} - ${movie.ruleName} - ${movie.fileSizeMb} MB",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchEventsTab(uiState: PendingUiState, viewModel: PendingViewModel) {
    if (uiState.watchEvents.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { Text("No missed watch events.", style = MaterialTheme.typography.bodyMedium) }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = viewModel::clearAllWatchEvents, enabled = !uiState.isActing) {
            Text("Clear All")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)
        ) {
            items(uiState.watchEvents, key = { it.id }) { item ->
                WatchEventRow(item, uiState.isActing, viewModel::processWatchEvent, viewModel::clearWatchEvent)
            }
        }
    }
}

@Composable
private fun WatchEventRow(
    item: PendingWatchEventItem,
    isActing: Boolean,
    onProcess: (String) -> Unit,
    onClear: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "${item.seriesTitle} - S${item.season}E${item.episode}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("${item.source} - ${item.user}", style = MaterialTheme.typography.bodySmall)
            Row(modifier = Modifier.padding(top = 8.dp)) {
                TextButton(onClick = { onProcess(item.id) }, enabled = !isActing) { Text("Process") }
                TextButton(onClick = { onClear(item.id) }, enabled = !isActing) { Text("Clear") }
            }
        }
    }
}
