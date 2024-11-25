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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.InventoryApplication
import com.example.inventory.data.movie.Movie
import com.example.inventory.ui.theme.light_gold


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
    var showDeleteDialog by remember { mutableStateOf(false) }
    val viewModel: LocalDetailViewModel = viewModel(factory = LocalDetailViewModelFactory(userListRepository,
        listMoviesRepository,
        movieRepository)
    )
    var showChangeRatingDialog by remember { mutableStateOf(false) }

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
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete movie"
                        )
                    }
                }
            )
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Deletion") },
                    text = { Text("Confirm movie deletion.") },
                    confirmButton = {
                        TextButton(onClick = {
                            // TODO: Delete the movie here
                            showDeleteDialog = false
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
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
                    .padding(16.dp)
                    .padding(top = 26.dp)
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
                            text = (movie?.releaseDate ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                        Row( // TODO: make prettier
                            modifier = Modifier
                                .clickable { showChangeRatingDialog = true } // display the dialog
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star Icon",
                                modifier = Modifier.size(40.dp),
                                tint = light_gold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = String.format("%.1f", movie?.userRating ?: 0.0), // Format with one decimal place
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 20.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Text(
                                text = " / 10",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.CenterVertically).padding(top = 2.dp),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
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
    if (showChangeRatingDialog) { // show the dialog for changing a rating
        var newRating by remember { mutableStateOf("%.1f".format(movie?.userRating!!.toFloat())) }
        var errorMessage by remember { mutableStateOf("") } // if its blank, then the user can submit their rating, otherwise no

        AlertDialog(
            onDismissRequest = { showChangeRatingDialog = false },
            title = { Text(text = "Your Rating") },
            text = {
                Column {
                    // Editable text field, border is invisible on focus and shows up when user clicks on it
                    OutlinedTextField(
                        shape = RoundedCornerShape(30.dp),
                        value = newRating,
                        onValueChange = {
                            if (it.length <= 4) { // longest string that can be inputted is 10.0
                                newRating = it
                                errorMessage =
                                    if (newRating.toFloatOrNull() != null && newRating.toFloat() in 0.0..10.0) {
                                        ""
                                    } else if (newRating.toFloatOrNull() != null && newRating.toFloat() !in 0.0..10.0) {
                                        "Rating must be between 0 and 10."
                                    } else {
                                        "Enter a valid number."
                                    }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal // pull up num pad for users
                        ),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .width(100.dp)
                            .align(Alignment.CenterHorizontally),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center
                        ),
                        colors = OutlinedTextFieldDefaults.colors( // make border color appear if input is clicked (focused)
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Unspecified,
                        ),

                    )
                    if (errorMessage.isNotEmpty()) { // display an error message preventing user from selecting "Change"
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    // Slider for changing rating value from 0.0 to 10.0
                    Slider(
                        value = if (newRating.toFloatOrNull() != null && newRating.toFloat() in 0.0..10.0) newRating.toFloat() else 0.0f,
                        onValueChange = { value ->
                            errorMessage = "" // clear error message since slider will always have valid input
                            newRating = "%.1f".format(value) // format to tens place
                        },
                        valueRange = 0.0f..10.0f,
                        steps = 99, // 10.0 - 0.0 divided by 0.1 gives 100 steps
                    )
                }
            },
            confirmButton = {
                Text(
                    "Change",
                    modifier = Modifier
                        .clickable {
                            if (errorMessage.isEmpty()) { // if there isn't an error message, let the user submit their rating
                                movie?.movieID?.let {
                                    viewModel.changeMovieRating(it, newRating.toFloat())
                                }
                                showChangeRatingDialog = false // close the dialog
                            }
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            },
            dismissButton = {
                Text(
                    "Cancel",
                    modifier = Modifier
                        .clickable {
                            showChangeRatingDialog = false // close the dialog
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            }
        )
    }
}
