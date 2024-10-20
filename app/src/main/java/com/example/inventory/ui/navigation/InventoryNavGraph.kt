package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.ui.home.DetailDestination
import com.example.inventory.ui.home.MovieDetailsScreen
import com.example.inventory.ui.home.SearchDestination
import com.example.inventory.ui.home.SearchScreen

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
        composable(
            route = DetailDestination.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType }) // Define the argument type
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: error("Missing movieID argument") // Get as Int
            MovieDetailsScreen(navController, movieId)
        }
    }
}
