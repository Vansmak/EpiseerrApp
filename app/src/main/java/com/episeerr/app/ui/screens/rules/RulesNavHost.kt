package com.episeerr.app.ui.screens.rules

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.episeerr.app.ui.screens.movierules.MovieRuleEditScreen
import com.episeerr.app.ui.screens.movierules.MoviesBrowserScreen

private const val HOME_ROUTE = "rules/home"
private const val CREATE_ROUTE = "rules/create"
private const val EDIT_ROUTE = "rules/edit/{ruleName}"
private const val MOVIE_CREATE_ROUTE = "movie-rules/create"
private const val MOVIE_EDIT_ROUTE = "movie-rules/edit/{ruleName}"
private const val MOVIES_BROWSE_ROUTE = "movies/browse?ruleName={ruleName}"
private const val SERIES_BROWSE_ROUTE = "series/browse?ruleName={ruleName}"

@Composable
fun RulesNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        composable(HOME_ROUTE) {
            RulesHomeScreen(
                onOpenRule = { name -> navController.navigate("rules/edit/$name") },
                onCreateRule = { navController.navigate(CREATE_ROUTE) },
                onOpenMovieRule = { name -> navController.navigate("movie-rules/edit/$name") },
                onCreateMovieRule = { navController.navigate(MOVIE_CREATE_ROUTE) },
                onBrowseMovies = { navController.navigate("movies/browse") },
                onBrowseSeries = { navController.navigate("series/browse") }
            )
        }
        composable(
            MOVIES_BROWSE_ROUTE,
            arguments = listOf(navArgument("ruleName") { type = NavType.StringType; nullable = true })
        ) {
            MoviesBrowserScreen(onBack = { navController.popBackStack() })
        }
        composable(
            SERIES_BROWSE_ROUTE,
            arguments = listOf(navArgument("ruleName") { type = NavType.StringType; nullable = true })
        ) {
            SeriesBrowserScreen(onBack = { navController.popBackStack() })
        }
        composable(CREATE_ROUTE) {
            RuleEditScreen(onBack = { navController.popBackStack() }, onManageSeries = {})
        }
        composable(
            EDIT_ROUTE,
            arguments = listOf(navArgument("ruleName") { type = NavType.StringType })
        ) {
            RuleEditScreen(
                onBack = { navController.popBackStack() },
                onManageSeries = { ruleName -> navController.navigate("series/browse?ruleName=$ruleName") }
            )
        }
        composable(MOVIE_CREATE_ROUTE) {
            MovieRuleEditScreen(onBack = { navController.popBackStack() }, onManageMovies = {})
        }
        composable(
            MOVIE_EDIT_ROUTE,
            arguments = listOf(navArgument("ruleName") { type = NavType.StringType })
        ) {
            MovieRuleEditScreen(
                onBack = { navController.popBackStack() },
                onManageMovies = { ruleName -> navController.navigate("movies/browse?ruleName=$ruleName") }
            )
        }
    }
}
