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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.data.api.getDetailsFromID
import com.example.inventory.R
import com.example.inventory.data.api.MovieDetails
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object DetailDestination {
    const val route = "movieDetails/{movieId}"

    fun createRoute(movieId: Int): String {
        return "movieDetails/$movieId"
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieDetailsScreen(navController: NavHostController, movieId: Int) {
    var movie by remember { mutableStateOf<MovieDetails?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = movieId) {
        coroutineScope.launch(Dispatchers.IO) { // Launch in IO thread
            movie = getDetailsFromID(movieId)
        }
    }
    Scaffold(
        //modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {

                    movie?.let {
                        Text(
                            text = it.title, // Replace with actual movie title when available
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
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
                        imageVector = Icons.Filled.Add, // You can change the icon
                        contentDescription = "Edit"
                    )
                },
                text = { Text("Add") },
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
                        model = "https://image.tmdb.org/t/p/w500${movie?.posterPath}",
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
                    movie?.let { it1 ->
                        Text(
                            text = it1.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    movie?.let { it1 ->
                        Text(
                            text = it1.releaseDate,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        text = movie?.runtime?.toString() ?: "", // Convert to String or use empty string if null
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = movie?.rating?.toString() ?: "", // Convert to String or use empty string if null
                        style = MaterialTheme.typography.bodyMedium
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
                movie?.let { it1 ->
                    Text(
                        text = it1.overview,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }





            // You can add more details like runtime, release date, rating, synopsis, etc. here
            // ...
        }
    }
}