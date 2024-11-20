package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.InventoryApplication
import com.example.inventory.data.movie.Movie


object LocalDetailDestination {
    const val ROUTE = "localMovieDetails/{movieId}"

    fun createRoute(movieId: Int): String {
        return "localMovieDetails/$movieId"
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DefaultLocale")
@Composable
fun LocalMovieDetailsScreen(navController: NavHostController, movieId: Int) {
    //var movie by remember { mutableStateOf<MovieDetails?>(null) }
    var movie by remember { mutableStateOf<Movie?>(null) }
    val userListRepository = InventoryApplication().container.userListRepository // use app container to get repository
    val listMoviesRepository = InventoryApplication().container.listMoviesRepository
    val movieRepository = InventoryApplication().container.movieRepository
    val viewModel: DetailViewModel = viewModel(factory = DetailViewModelFactory(userListRepository,
        listMoviesRepository,
        movieRepository)
    )

    // collect data from ListScreenViewModel
    //val allLists by viewModel.allLists.collectAsState()
    //val currList = selectedList?.listName // used for highlighting selection in bottom sheet

//    var movie_to_add by remember { mutableStateOf<Movie?>(null) } // Make this a state

// Fetch movie details from the local database using movieId
    LaunchedEffect(key1 = movieId) {
        movieRepository.getMovieStream(movieId).collect {
            movie = it
        }
    }
    Scaffold(
        //modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    // Movie title in top bar
                    movie?.let {
                        Text(
                            text = it.title,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                // Back icon
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(ListDestination.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) {
        Column{
            // Image and text in a Row
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp).padding(top = 26.dp)
                    .padding(top = TopAppBarDefaults.TopAppBarExpandedHeight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top // Align to the top of the row
                ) {
                    Card { // Card for the image
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${movie?.posterPath}",
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { }
                                .width(170.dp)
                                .aspectRatio(0.6667f),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        movie?.let { it1 ->
                            Text(
                                text = it1.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                        Text(
                            text = (movie?.runtime?.toString() ?: "") + " minutes",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                        Text(
                            text = String.format("%.1f/10", movie?.userRating ?: 0.0), // Format with one decimal place
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                        Text(
                            text = (movie?.releaseDate ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(all = 15.dp)) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    movie?.let { it1 ->
                        Text(
                            text = it1.overview ?: "", // Provide a default value if overview is null
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}