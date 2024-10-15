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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        text = "Title",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "2024",
                        style = MaterialTheme.typography.bodyMedium,
                        //fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "1h30m",
                        style = MaterialTheme.typography.bodyMedium,
                        //fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "PG-13",
                        style = MaterialTheme.typography.bodyMedium,
                        //fontWeight = FontWeight.Bold,
                    )
                }
            }




            // You can add more details like runtime, release date, rating, synopsis, etc. here
            // ...
        }
    }
}