package com.episeerr.app.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val HOME_ROUTE = "settings/home"
private const val LOGS_ROUTE = "settings/logs"

@Composable
fun SettingsNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        composable(HOME_ROUTE) {
            GlobalSettingsScreen(onOpenLogs = { navController.navigate(LOGS_ROUTE) })
        }
        composable(LOGS_ROUTE) {
            LogsScreen(onBack = { navController.popBackStack() })
        }
    }
}
