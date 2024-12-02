package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inventory.R
import com.example.inventory.data.SettingsDataStore
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavHostController, modifier: Modifier = Modifier) {

    var materialYouEnabled by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("Light") }
    val themes = listOf("Light", "Dark", "System Default")
    var startTab by remember { mutableStateOf("Home") }
    val tabOptions = listOf("List", "Discover", "Settings")
    val context = navController.context
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp
                ),
                title = { Text("Settings") },
            )
        },

        content = {innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DropDownMenuSettings(
                    label = "Theme",
                    options = themes,
                    selectedOption = selectedTheme,
                    onSelect = { selectedTheme = it}
                )

                DropDownMenuSettings(
                    label = "Start Screen",
                    options = tabOptions,
                    selectedOption = startTab,
                    onSelect = { tab ->
                        startTab = tab
                        coroutineScope.launch {
                            SettingsDataStore.saveDefault(context, tab)
                        }
                    }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Automatic Wallpaper Theming",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = false, onCheckedChange = { materialYouEnabled = it })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Haptic Feedback",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = false, onCheckedChange = {  })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Push Notifications",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = false, onCheckedChange = {  })
                }

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Export Lists to Google Drive")
                }
            }
        }
    )
}

@Composable
fun DropDownMenuSettings(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedOption)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false}
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                        text = { Text(option) }
                    )
                }
            }
        }
    }
}