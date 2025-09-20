package com.example.filmflow.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.filmflow.ui.movielist.DetailDestination
import com.example.filmflow.ui.movielist.ListDestination
import com.example.filmflow.ui.movielist.ListScreen
import com.example.filmflow.ui.movielist.MovieDetailsScreen
import com.example.filmflow.ui.discover.SearchDestination
import com.example.filmflow.ui.discover.SearchScreen
import com.example.filmflow.ui.settings.SettingsDestination
import com.example.filmflow.ui.settings.SettingsScreen
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.filmflow.ui.showlist.ShowDetailDestination
import com.example.filmflow.ui.showlist.ShowDetailsScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun FilmFlowNavHost(
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
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = SearchDestination.route,
            enterTransition = {
                if (this.initialState.destination.route == DetailDestination.ROUTE || this.initialState.destination.route == SettingsDestination.route) {
                    EnterTransition.None
                } else {
                    fadeIn() + slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Slide in from the right
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                }
            },
            exitTransition = {
                when (this.targetState.destination.route) {
                    ListDestination.route -> {
                        fadeOut() + slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth }, // Slide out to the right
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )
                    }
                    DetailDestination.ROUTE -> {
                        ExitTransition.None
                    }
                    SettingsDestination.route -> {
                        ExitTransition.None
                    }
                    else -> {
                        fadeOut() + slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth }, // Slide out to the left
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )
                    }
                }
            },
            popEnterTransition = {
                if (this.initialState.destination.route == DetailDestination.ROUTE || this.initialState.destination.route == SettingsDestination.route) {
                    EnterTransition.None
                } else {
                    fadeIn() + slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Slide in from the right
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                }
            },
            popExitTransition = {
                fadeOut() + slideOutHorizontally(
                    targetOffsetX = {fullWidth -> -fullWidth},
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            }

        ) {
            SearchScreen(navController)
        }

        composable(
            route = DetailDestination.ROUTE,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            },
            exitTransition = {
                if (this.targetState.destination.route == DetailDestination.ROUTE) {
                    ExitTransition.None // No transition!
                } else {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                }
            },
            popEnterTransition = {
                fadeIn()
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            }
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: error("Missing movieID argument")
            MovieDetailsScreen(navController, movieId)
        }

        composable(
            route = ShowDetailDestination.ROUTE,
            arguments = listOf(navArgument("showId") { type = NavType.IntType }),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            },
            exitTransition = {
                if (this.targetState.destination.route == ShowDetailDestination.ROUTE) {
                    ExitTransition.None // No transition!
                } else {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                }
            },
            popEnterTransition = {
                fadeIn()
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            }
        ) { backStackEntry ->
            val showId = backStackEntry.arguments?.getInt("showId") ?: error("Missing showID argument")
            ShowDetailsScreen(navController, showId)
        }

        composable(
            route = ListDestination.route,
            enterTransition = {
                if (this.initialState.destination.route == DetailDestination.ROUTE || this.initialState.destination.route == SettingsDestination.route) {
                    EnterTransition.None
                } else {
                    fadeIn() + slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                }
            }, // Optional: Add to other routes if you want
            exitTransition = {
                if (this.targetState.destination.route == DetailDestination.ROUTE) {
                    ExitTransition.None
                } else {
                    fadeOut() + slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth }, // Slide out to the left
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                }
            }
        ) {
            ListScreen(navController)
        }

        composable(
            route = SettingsDestination.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            },  // Optional
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            }
        ) {
            SettingsScreen(navController)
        }
    }
}
