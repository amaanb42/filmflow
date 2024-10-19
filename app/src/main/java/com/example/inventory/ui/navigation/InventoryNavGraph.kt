package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.inventory.ui.home.DetailDestination
import com.example.inventory.ui.home.MovieDetailsScreen
import com.example.inventory.ui.home.SearchDestination
import com.example.inventory.ui.home.ListDestination
import com.example.inventory.ui.home.SearchScreen
import com.example.inventory.ui.home.ListScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = SearchDestination.route, // Set SearchDestination as the start destination
        modifier = modifier
    ) {
        composable(route = SearchDestination.route) {
            SearchScreen(navController)
            // Pass navController to SearchScreen
        }
        composable(route = DetailDestination.route) {
            MovieDetailsScreen(navController)
        }

        composable(route = ListDestination.route) {
            ListScreen(navController)
        }
    }
}
