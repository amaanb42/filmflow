package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination

object ListDestination : NavigationDestination {
    override val route = "list"
    override val titleRes = R.string.list_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun ListScreen(navController: NavHostController){
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Planning"
                    )
                },

                actions = {
                    IconButton(onClick = { /*Handle search*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(onClick = {/* TODO: Some shit idk yet */}) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More Stuff"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Handle FAB click */ },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Create, // You can change the icon
                        contentDescription = "Edit"
                    )
                },
                text = { Text("Planning") },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.offset(y = -100.dp)
            )
        }
    ) {
        Column(modifier = Modifier.offset(y = 110.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {/*TODO: Sorting*/},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(0.dp),

                ) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Sorting")
                    Text("Sort")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {/*TODO: View*/},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "View")
                }
            }
        }
    }
}