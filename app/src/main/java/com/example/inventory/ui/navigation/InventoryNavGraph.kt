package com.example.inventory.ui.navigation

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.ui.home.DetailDestination
import com.example.inventory.ui.home.ListDestination
import com.example.inventory.ui.home.ListScreen
import com.example.inventory.ui.home.LocalDetailDestination
import com.example.inventory.ui.home.LocalMovieDetailsScreen
import com.example.inventory.ui.home.MovieDetailsScreen
import com.example.inventory.ui.home.SearchDestination
import com.example.inventory.ui.home.SearchScreen
import com.example.inventory.ui.home.SettingsDestination
import com.example.inventory.ui.home.SettingsScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    defaultTab: String
) {

    val startDestination = when (defaultTab) {
        "Discover" -> SearchDestination.route
        "List" -> ListDestination.route
        "Settings" -> SettingsDestination.route
        else -> SearchDestination.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination, // Set SearchDestination as the start destination
        modifier = modifier
    ) {
        composable(route = SearchDestination.route) {
            SearchScreen(navController)
            // Pass navController to SearchScreen
        }

        composable(
            route = DetailDestination.ROUTE,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType }) // Define the argument type
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: error("Missing movieID argument")
            MovieDetailsScreen(navController, movieId)
        }

        composable(
            route = LocalDetailDestination.ROUTE, // New route for LocalDetailScreen
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType },
                navArgument("currList") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
                ?: error("Missing movieID argument")
            val currListName = backStackEntry.arguments?.getString("currList")
                ?: error("Missing currList argument")
            LocalMovieDetailsScreen(navController, movieId)
        }

        composable(route = ListDestination.route) {
            ListScreen(navController)
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(navController)
        }
    }
}
