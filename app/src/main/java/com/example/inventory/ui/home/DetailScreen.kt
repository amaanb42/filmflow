package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.data.api.MovieDetails
import com.example.inventory.data.api.getDetailsFromID
import com.example.inventory.data.movie.Movie
import com.example.inventory.ui.theme.dark_pine
import com.example.inventory.ui.theme.material_red
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object DetailDestination {
    const val ROUTE = "movieDetails/{movieId}"

    fun createRoute(movieId: Int): String {
        return "movieDetails/$movieId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DefaultLocale")
@Composable
fun MovieDetailsScreen(navController: NavHostController, movieId: Int) {
    var movieDetails by remember { mutableStateOf<MovieDetails?>(null) } // Renamed for clarity
    val userListRepository = InventoryApplication().container.userListRepository // use app container to get repository
    val listMoviesRepository = InventoryApplication().container.listMoviesRepository
    val movieRepository = InventoryApplication().container.movieRepository
    val viewModel: DetailViewModel = viewModel(factory = DetailViewModelFactory(userListRepository,
        listMoviesRepository,
        movieRepository,
        movieId)
    )
    val coroutineScope = rememberCoroutineScope()

    // collect data from ListScreenViewModel
    val listsMovieIn by viewModel.listsForMovie.collectAsState()
    var movieToAdd by remember { mutableStateOf<Movie?>(null) } // Make this a state
    var expanded by remember { mutableStateOf(false) } // State for expanding synopsis
    val isInList by remember { derivedStateOf { "Planning" in listsMovieIn || "Watching" in listsMovieIn || "Completed" in listsMovieIn } }

    //Alter code below to fetch from local database instead of using the TMDB function
    LaunchedEffect(key1 = movieId) {
        coroutineScope.launch(Dispatchers.IO) { // Launch in IO thread
            movieDetails = getDetailsFromID(movieId)
            // Update movie_to_add after movie is loaded
            movieToAdd= Movie(
                movieId,
                movieDetails?.title ?: "", // Provide an empty string if title is null
                movieDetails?.overview,
                "", // Provide an empty string for director since it's missing
                movieDetails?.posterPath ?: "", // Provide an empty string if posterPath is null
                movieDetails?.releaseDate,
                movieDetails?.runtime,
                //movieDetails?.rating?.toFloat(), // Convert Double? to Float?
                0.0.toFloat(), // Sends 0.0 to localDB instead of TMDB rating
                emptyList() // Provide an empty list for genres
            )
        }
    }
    Scaffold(
        //modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp
                ),
                title = {
                    // Movie title in top bar
                    movieDetails?.let {
                        Text(
                            text = it.title, // Replace with actual movie title when available
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                // Back icon
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        // Add movie to list FAB
        floatingActionButton = {
            movieToAdd?.let { movie -> // Use movie here for clarity
                FloatingActionButton(
                    onClick = {
                        if (isInList) {
                            // Delete the movie from whichever list it's in
                            viewModel.deleteMovie(movieId) // New function in ViewModel

                        } else {
                            // Add the movie to the "Planning" list
                            viewModel.addMovieToList("Planning", movie)
                        }
                    },
                    containerColor = if (isInList) material_red else dark_pine,
                    contentColor = Color.White,
                    modifier = Modifier.offset(y = (20).dp)
                ) { // Icon is set directly in the content lambda
                    // Use AnimatedContent to smoothly transition between icons
                    AnimatedContent(
                        targetState = isInList,
                        transitionSpec = {
                            scaleIn(animationSpec = tween(400)) togetherWith scaleOut(animationSpec = tween(400))
                        }
                    ) { targetState ->
                        val icon = if (targetState) {
                            Icons.Filled.Delete
                        } else {
                            Icons.Filled.Add
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = if (targetState) "Remove from List" else "Add to List"
                        )
                    }
                }
            }
        }
    ) {
        LazyColumn {
            // Image and text in a Row
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        //.padding(top = 26.dp)
                        .padding(top = TopAppBarDefaults.TopAppBarExpandedHeight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top // Align to the top of the row
                    ) {
                        Card { // Card for the image
                            SubcomposeAsyncImage(
                                model = "https://image.tmdb.org/t/p/w500${movieDetails?.posterPath}",
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable { }
                                    .width(170.dp)
                                    .aspectRatio(0.6667f),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(0.6667f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                },
//                            error = {
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .aspectRatio(0.6667f),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    CircularProgressIndicator()
//                                }
//                            }
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            movieDetails?.let { it1 ->
                                Text(
                                    text = it1.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                            Text(
                                text = formatRuntime(movieDetails?.runtime),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                            if (movieDetails != null) {
                                // Check if releaseDate is not null or empty
                                val releaseDate = movieDetails?.releaseDate
                                if (!releaseDate.isNullOrEmpty()) {
                                    val originalDate = LocalDate.parse(
                                        releaseDate,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    )
                                    val formattedDate = originalDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                                    Text(
                                        text = formattedDate,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    // Handle the case where releaseDate is null or empty
                                    Text(
                                        text = "N/A", // Fallback text
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                //Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(bottom = 12.dp)
                                ) {
                                    RatingCircle(
                                        userRating = (movieDetails?.rating)?.toFloat() ?: 0.0f, // Add ? before toFloat()
                                        fontSize = 28.sp,
                                        radius = 50.dp,
                                        animDuration = 1000,
                                        strokeWidth = 8.dp
                                    )
                                }
                                //Spacer(modifier = Modifier.weight(1f))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Community Average",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = isInList,
                    enter = slideInVertically() + fadeIn(), // Or other animations
                    exit = slideOutVertically() + fadeOut()  // Or other animations
                ) {
                    Column {
                        StatusButtons(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .then( // 'then' to conditionally applies clickable
                            if ((movieDetails?.overview?.length ?: 0) > 250) { // if synopsis length is large, allow expand clickable
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { expanded = !expanded }
                            } else {
                                Modifier // Do not apply clickable if overview is short
                            }
                        )
                        .animateContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(15.dp)
                            .animateContentSize() // Add this modifier
                    ) {
                        Text(
                            text = "Synopsis",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (movieDetails?.overview?.isEmpty() == true) { // Check if the list is empty
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { Text("Not available.") }
                        } else {
                            movieDetails?.let { it1 ->
//                                AnimatedVisibility(
//                                    visible = expanded,
//                                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
//                                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300)),
//                                    modifier = Modifier.wrapContentHeight() // Ensure it wraps content
//                                ) {
//
//                                }
                                if (expanded) {
                                    Text(
                                        text = it1.overview,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.clickable { expanded = false }
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .clickable { expanded = true }
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (it1.overview.length > 250) "${it1.overview.substring(0, 250)}..." else it1.overview,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
//                                            Icon(
//                                                imageVector = Icons.Default.ArrowDropDown,
//                                                contentDescription = "Expand",
//                                                modifier = Modifier.align(Alignment.CenterVertically)
//                                            )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { // Card for movie actors
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Cast",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp),
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Bold
                        )
                        if (viewModel.movieCast.isEmpty()) { // Check if the list is empty
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { Text("Not available.") }
                        } else {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(viewModel.movieCast) { castMember ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Card {
                                            SubcomposeAsyncImage(
                                                model = "https://image.tmdb.org/t/p/w500${castMember.posterPath}",
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(124.dp)
                                                    .aspectRatio(0.666667f),
                                                contentScale = ContentScale.Fit,
                                                loading = {
                                                    Box(
                                                        modifier = Modifier.size(24.dp), // changes size of loading icon
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator()
                                                    }
                                                },
                                                error = {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.image), // Replace with your icon resource
                                                        contentDescription = "Image not available",
                                                        //modifier = Modifier.size(6.dp) // Adjust size as needed
                                                    )
                                                }
                                            )
                                        }
                                        Text(
                                            text = castMember.realName,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(130.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = castMember.characterName,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(130.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp) // Consistent padding on all sides
                ) {
                    Column { // Use a Column to structure the content
                        Text(
                            text = "Recommended",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp), // Consistent padding
                            textAlign = TextAlign.Left,
                            //fontSize = 16.sp,
                            fontWeight = FontWeight.Bold // Add FontWeight for emphasis
                        )

                        if (viewModel.recommendedMovies.isEmpty()) { // Check if the list is empty
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { Text("Not available.") }
                            } else {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp), // Consistent padding
                                    contentPadding = PaddingValues(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        viewModel.recommendedMovies,
                                        key = { movie -> movie.id }) { movie ->
                                        Card {
                                            SubcomposeAsyncImage(
                                                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .clickable {
                                                        navigateToMovieDetails(
                                                            navController,
                                                            movie.id
                                                        )
                                                    }
                                                    .width(124.dp)
                                                    .aspectRatio(0.6667f),
                                                contentScale = ContentScale.Crop,
                                                loading = {
                                                    Box(
                                                        modifier = Modifier.size(24.dp), // changes size of loading icon
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator()
                                                    }
                                                },
                                                error = {
                                                    Text("Image not available")
                                                }
                                            )
                                        }
                                    }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun SegmentedButtons(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier,
) {
    val listsForMovie by viewModel.listsForMovie.collectAsState()
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val items = listOf(
        R.drawable.planning_icon,
        R.drawable.watching_icon,
        R.drawable.completed_icon
    )
    val listNames = listOf("Planning", "Watching", "Completed")

    // Determine the initial selected index based on the movie's current list
    val status: String =
        if ("Completed" in listsForMovie)
            "Completed"
        else if ("Watching" in listsForMovie)
            "Watching"
        else
            "Planning"

    // Set the initial selected index based on the status
    LaunchedEffect(key1 = status) {
        selectedItemIndex = listNames.indexOf(status)
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            key(selectedItemIndex) { // Add this key
                ListIconButton(
                    icon = item,
                    isSelected = selectedItemIndex == index,
                    onClick = {
                        val currentList =
                            listNames.find { it in listsForMovie } // Find the current list
                        val newList = listNames[index]
                        if (currentList != null && currentList != newList) {
                            viewModel.moveMovieToList(
                                currentList,
                                newList
                            ) // Move only if necessary
                        }
                        selectedItemIndex = index
                    },
                    label = listNames[index] // displays list name for icon
                )
            }
            if (index < items.size - 1) {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}

@Composable
fun ListIconButton(
    icon: Int, // Changed to Int for drawable resource ID
    isSelected: Boolean,
    onClick: () -> Unit,
    label: String, //label for buttons
) {
//    val backgroundColor = if (isSelected) {
//        MaterialTheme.colorScheme.primary
//    } else {
//        MaterialTheme.colorScheme.surfaceVariant
//    }
//
//    val iconTint = if (isSelected) {
//        MaterialTheme.colorScheme.onPrimary
//    } else {
//        MaterialTheme.colorScheme.onSurfaceVariant
//    }

    val animatedSurfaceColor = animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(
            durationMillis = 300, // Adjust the duration as needed
            easing = FastOutSlowInEasing // Use an easing function for smoother transitions
        )
    )

    val animatedIconColor = animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(
            durationMillis = 300, // Adjust the duration as needed
            easing = FastOutSlowInEasing // Use an easing function for smoother transitions
        )
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        key(isSelected, animatedSurfaceColor.value) { // Add this key
            Surface(
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = onClick,
                        role = Role.RadioButton
                    ),
                shape = MaterialTheme.shapes.small,
                color = animatedSurfaceColor.value
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = animatedIconColor.value
                    )
                }
            }
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 4.dp),
            color = animatedIconColor.value,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    }
}

@Composable
fun StatusButtons(
    viewModel: DetailViewModel // Add the ViewModel
) {
    Column {
        SegmentedButtons(viewModel = viewModel)
    }
}