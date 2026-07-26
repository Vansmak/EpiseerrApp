package com.episeerr.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.episeerr.app.ui.screens.dashboard.DashboardScreen
import com.episeerr.app.ui.screens.rules.RulesNavHost
import com.episeerr.app.ui.screens.services.ServicesScreen
import com.episeerr.app.ui.screens.settings.GlobalSettingsScreen

private sealed class MainTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : MainTab("main/dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Rules : MainTab("main/rules", "Rules", Icons.Filled.List)
    object Services : MainTab("main/services", "Services", Icons.Filled.Extension)
    object Settings : MainTab("main/settings", "Settings", Icons.Filled.Settings)
}

private val mainTabs = listOf(MainTab.Dashboard, MainTab.Rules, MainTab.Services, MainTab.Settings)

@Composable
fun MainScaffold() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                mainTabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainTab.Dashboard.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(MainTab.Dashboard.route) { DashboardScreen() }
            composable(MainTab.Rules.route) { RulesNavHost() }
            composable(MainTab.Services.route) { ServicesScreen() }
            composable(MainTab.Settings.route) { GlobalSettingsScreen() }
        }
    }
}
