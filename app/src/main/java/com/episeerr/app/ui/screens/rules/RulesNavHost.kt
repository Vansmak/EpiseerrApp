package com.episeerr.app.ui.screens.rules

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private const val LIST_ROUTE = "rules/list"
private const val CREATE_ROUTE = "rules/create"
private const val EDIT_ROUTE = "rules/edit/{ruleName}"

@Composable
fun RulesNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LIST_ROUTE) {
        composable(LIST_ROUTE) {
            RulesListScreen(
                onOpenRule = { name -> navController.navigate("rules/edit/$name") },
                onCreateRule = { navController.navigate(CREATE_ROUTE) }
            )
        }
        composable(CREATE_ROUTE) {
            RuleEditScreen(onBack = { navController.popBackStack() })
        }
        composable(
            EDIT_ROUTE,
            arguments = listOf(navArgument("ruleName") { type = NavType.StringType })
        ) {
            RuleEditScreen(onBack = { navController.popBackStack() })
        }
    }
}
