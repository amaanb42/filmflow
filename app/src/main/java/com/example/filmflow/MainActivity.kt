package com.example.filmflow

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.filmflow.data.SettingsDataStore
import com.example.filmflow.ui.discover.SearchDestination
import com.example.filmflow.ui.movielist.ListDestination
import com.example.filmflow.ui.navigation.FilmFlowNavHost
import com.example.filmflow.ui.theme.FilmFlowTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
)

@Suppress("NAME_SHADOWING")
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            FilmFlowTheme {
                val items = listOf(
                    BottomNavigationItem(
                        title = "Your Movies",
                        selectedIcon = painterResource(id = R.drawable.movie_fill),
                        unselectedIcon = painterResource(id = R.drawable.movie_no_fill),
                    ),
                    BottomNavigationItem(
                        title = "Discover",
                        selectedIcon = painterResource(id = R.drawable.explore_fill),
                        unselectedIcon = painterResource(id = R.drawable.explore),
                    ),
                    BottomNavigationItem(
                        title = "Your Shows (WIP)",
                        selectedIcon = painterResource(id = R.drawable.show_fill),
                        unselectedIcon = painterResource(id = R.drawable.show_no_fill),
                    )
                )

                val navController = rememberNavController()
                var selectedItemIndex by remember { mutableIntStateOf(1) } // 0, 1, 2 changes which navbar icon is highlighted at startup

                // Determine the current route
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                val coroutineScope = rememberCoroutineScope()
                var defaultTab by remember { mutableStateOf("Discover") }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        defaultTab = SettingsDataStore.getDefaultTab(applicationContext).first()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Scaffold(
                        bottomBar = {
                            // Show the NavigationBar only on specified screens
                            // Use AnimatedVisibility for the NavigationBar
                            AnimatedVisibility(
                                visible = currentRoute in listOf(SearchDestination.route, ListDestination.route), //add ListDestination.route and SettingsDestination.route in comma separated list
                                enter = slideInVertically(
                                    // Start the slide from below the screen
                                    initialOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 300)
                                ),
                                exit = slideOutVertically(
                                    // Exit towards the bottom of the screen
                                    targetOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 300)
                                )
                            ) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    tonalElevation = 0.dp
                                ) {
                                    items.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = {
                                                selectedItemIndex = index
                                                try {
                                                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                                                    when (index) {
                                                        0 -> {
                                                            if (currentRoute != ListDestination.route) {
                                                                navController.navigate(ListDestination.route) {
                                                                    popUpTo(SearchDestination.route) {
                                                                        inclusive = true
                                                                        saveState = true
                                                                    } // Pop up to Search, but don't pop Search itself
                                                                    restoreState = true
                                                                }
                                                            }
                                                        }
                                                        1 -> {
                                                            if (currentRoute != SearchDestination.route) {
                                                                navController.navigate(SearchDestination.route) {
                                                                    popUpTo(ListDestination.route) {
                                                                        inclusive = true
                                                                        saveState = true
                                                                    } // Pop up to List, but don't pop List itself
                                                                    restoreState = true
                                                                }
                                                            }
                                                        }
                                                        2 -> {
                                                            // to do
                                                        }
                                                        // ... other destinations ...
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("NavigationError", "Error navigating to index $index", e)
                                                }
                                            }
                                            ,
                                            label = { Text(text = item.title) },
                                            alwaysShowLabel = false,
                                            icon = {
                                                Icon(
                                                    //imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                                    painter = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { //innerPadding ->
                        FilmFlowNavHost(
                            navController = navController,
                            //modifier = Modifier.padding(innerPadding),
                            defaultTab = defaultTab
                        )
                    }
                }
            }
        }
    }
}
