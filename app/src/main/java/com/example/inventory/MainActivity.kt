package com.example.inventory

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.inventory.ui.theme.InventoryTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.inventory.ui.navigation.InventoryNavHost
import com.example.inventory.ui.home.SearchDestination
import com.example.inventory.ui.home.ListDestination

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@Suppress("NAME_SHADOWING")
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            InventoryTheme {
                val items = listOf(
                    BottomNavigationItem(
                        title = "List",
                        selectedIcon = Icons.AutoMirrored.Filled.List,
                        unselectedIcon = Icons.AutoMirrored.Outlined.List,
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Discover",
                        selectedIcon = Icons.Filled.Search,
                        unselectedIcon = Icons.Outlined.Search,
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Stats",
                        selectedIcon = Icons.Filled.Star,
                        unselectedIcon = Icons.Outlined.Star,
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        hasNews = false,
                        badgeCount = null
                    )
                )

                val navController = rememberNavController()
                var selectedItemIndex by rememberSaveable { mutableIntStateOf(1) } // 0, 1, 2 changes which navbar icon is highlighted at startup

                // Determine the current route
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
                                    containerColor = MaterialTheme.colorScheme.surface,
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
                                                        0 -> if (currentRoute != ListDestination.route) navController.navigate(ListDestination.route)
                                                        1 -> if (currentRoute != SearchDestination.route) navController.navigate(SearchDestination.route)
                                                        //2 -> if (currentRoute != SettingsDestination.route) navController.navigate(SettingsDestination.route)

                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("NavigationError", "Error navigating to index $index", e)
                                                }
                                            },
                                            label = { Text(text = item.title) },
                                            alwaysShowLabel = false,
                                            icon = {
                                                BadgedBox(
                                                    badge = {
                                                        if (item.badgeCount != null) {
                                                            Badge { Text(text = item.badgeCount.toString()) }
                                                        } else if (item.hasNews) {
                                                            Badge()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                                        contentDescription = item.title
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        // Replace InventoryApp with InventoryNavHost
                        InventoryNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
