package com.episeerr.app.ui.screens.overview

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private const val HOME_ROUTE = "overview/home"
private const val CONFIG_ROUTE = "overview/service/{serviceKey}"

@Composable
fun OverviewNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        composable(HOME_ROUTE) {
            OverviewScreen(
                onConfigureService = { key -> navController.navigate("overview/service/$key") }
            )
        }
        composable(
            CONFIG_ROUTE,
            arguments = listOf(navArgument("serviceKey") { type = NavType.StringType })
        ) {
            ServiceConfigScreen(onBack = { navController.popBackStack() })
        }
    }
}
