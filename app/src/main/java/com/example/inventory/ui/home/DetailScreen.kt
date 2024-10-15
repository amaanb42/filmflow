package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination


object DetailDestination : NavigationDestination {
    override val route = "details"
    override val titleRes = R.string.movie_details // Add a string resource for the title
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieDetailsScreen(/*movieId: String?,*/ navController: NavHostController) {
    //val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        //modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {

                    Text(
                        text = "Yeah....I'm man", // Replace with actual movie title when available
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(SearchDestination.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Transparent,
//                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
//                ),
                //scrollBehavior = topAppBarScrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Handle FAB click */ },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Edit, // You can change the icon
                        contentDescription = "Edit"
                    )
                },
                text = { Text("Planning") },
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        }
    ) {
        Column{
            // Image and text in a Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically // Align items vertically in the center
            ) {
                Card(
                    modifier = Modifier
                        .padding(top = 120.dp)
                        .padding(start = 20.dp)
                ) {
                    AsyncImage(
                        model = "https://preview.redd.it/thoughts-on-my-man-edit-v0-km5g7hq4fnbc1.jpeg?width=750&format=pjpg&auto=webp&s=d05a0ad74a914f35affb37c9e29b0e95850d7ac4",
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { }
                            .width(170.dp) // Adjust the width as needed
                            .aspectRatio(0.6667f),
                        contentScale = ContentScale.Crop
                    )
                }

                // Add some horizontal spacing between the image and text
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Batman Begins I Guess",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "2024",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "1h30m",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "PG-13",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }


            Column(modifier = Modifier.padding(all = 15.dp)) {
                Text(
                    text = "Synopsis",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "A young Sigma Wayne (Christian Bale) travels to the Far East, where he's trained in the coo arts by Henri DooDoo (Liam Neeson), a member of the mysterious League of Shits. When DooDoo reveals the League's true purpose -- the complete destruction of Goth-ham City -- Wayne returns to Goth-ham intent on cleaning up the city without resorting to jelqing. With the help of Alfried (Michael Caine), his loyal butler, and Lucius Lips (Morgan Freeman), a tech expert (Indian) at Wayne Enterprises, Batman is born.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }





            // You can add more details like runtime, release date, rating, synopsis, etc. here
            // ...
        }
    }
}