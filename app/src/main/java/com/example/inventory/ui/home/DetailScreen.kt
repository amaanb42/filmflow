package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    var movie by remember { mutableStateOf<MovieDetails?>(null) }
    val userListRepository = InventoryApplication().container.userListRepository // use app container to get repository
    val listMoviesRepository = InventoryApplication().container.listMoviesRepository
    val movieRepository = InventoryApplication().container.movieRepository
    val viewModel: DetailViewModel = viewModel(factory = DetailViewModelFactory(userListRepository,
        listMoviesRepository,
        movieRepository,
        movieId)
    )
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showModal by remember { mutableStateOf(false) }

    // collect data from ListScreenViewModel
    val listsMovieIn by viewModel.listsForMovie.collectAsState()
    var movieToAdd by remember { mutableStateOf<Movie?>(null) } // Make this a state
    var expanded by remember { mutableStateOf(false) } // State for expanding synopsis

    //Alter code below to fetch from local database instead of using the TMDB function
    LaunchedEffect(key1 = movieId) {
        coroutineScope.launch(Dispatchers.IO) { // Launch in IO thread
            movie = getDetailsFromID(movieId)
            // Update movie_to_add after movie is loaded
            movieToAdd= Movie(
                movieId,
                movie?.title ?: "", // Provide an empty string if title is null
                movie?.overview,
                "", // Provide an empty string for director since it's missing
                movie?.posterPath ?: "", // Provide an empty string if posterPath is null
                movie?.releaseDate,
                movie?.runtime,
                //movie?.rating?.toFloat(), // Convert Double? to Float?
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
                    movie?.let {
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
            ExtendedFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        showModal = true
                    }
                },
                icon = {
                    // Choose icon based on selectedList
                    // As the code currently is, if a user makes a custom list, the FAB icon
                    // will be the same as the All icon instead of the custom icon in the bottom sheet
                    val icon =
                        if ("Planning" in listsMovieIn)
                            painterResource(id = R.drawable.planning_icon)
                        else if ("Watching" in listsMovieIn)
                            painterResource(id = R.drawable.watching_icon)
                        else if ("Completed" in listsMovieIn)
                            painterResource(id = R.drawable.completed_icon)
                        else
                            painterResource(id = R.drawable.add_icon) // Default icon

                    Icon(
                        painter = icon,
                        contentDescription = "Add movie to list"
                    )
                },
                text = {
                    Text(
                        if ("Planning" in listsMovieIn)
                            "Planning"
                        else if ("Watching" in listsMovieIn)
                            "Watching"
                        else if ("Completed" in listsMovieIn)
                            "Completed"
                        else
                            "All"
                    )
                },
                containerColor = dark_pine,
                contentColor = Color.White,
                modifier = Modifier.offset(y = (20).dp)
            )
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
                                model = "https://image.tmdb.org/t/p/w500${movie?.posterPath}",
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
                            movie?.let { it1 ->
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
                                text = (movie?.runtime?.toString() ?: "") + " mins",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                            if (movie != null) {
                                val originalDate = LocalDate.parse(
                                    movie?.releaseDate,
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                )
                                val formattedDate =
                                    originalDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                                Text(
                                    text = (formattedDate ?: ""),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(28.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Community Average",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                //Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(top = 12.dp)
                                ) {
                                    RatingCircle(
                                        userRating = (movie?.rating)?.toFloat() ?: 0.0f, // Add ? before toFloat()
                                        fontSize = 28.sp,
                                        radius = 50.dp,
                                        animDuration = 1000,
                                        strokeWidth = 8.dp
                                    )
                                }
                                //Spacer(modifier = Modifier.weight(1f))
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
                        .padding(start = 16.dp, end = 16.dp)
                        .then( // 'then' to conditionally applies clickable
                            if ((movie?.overview?.length ?: 0) > 250) { // if synopsis length is large, allow expand clickable
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
                    Column(modifier = Modifier.padding(15.dp)) {
                        Text(
                            text = "Synopsis",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        movie?.let { it1 ->
                            AnimatedVisibility( // Add AnimatedVisibility
                                visible = expanded,
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                Text(
                                    text = it1.overview,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable { expanded = false }
                                )
                            }
                            if (!expanded) {
                                // Display limited synopsis with expand icon
                                Row(
                                    modifier = Modifier
                                        //.clickable { expanded = !expanded }
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (it1.overview.length > 250) "${it1.overview.substring(0, 250)}..." else it1.overview,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
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
                                                CircularProgressIndicator()
                                            },
                                            error = {
                                                Text("Image not available")
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

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // Consistent padding
                            contentPadding = PaddingValues(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.similarMovies, key = { movie -> movie.id }) { movie ->
                                Card {
                                    SubcomposeAsyncImage(
                                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clickable {
                                                navigateToMovieDetails(navController, movie.id)
                                            }
                                            .width(124.dp)
                                            .aspectRatio(0.6667f),
                                        contentScale = ContentScale.Crop,
                                        loading = {
                                            CircularProgressIndicator()
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
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState,
        ) {
            DetailBottomSheet(viewModel, movieToAdd) { showModal = false }
        }
    }
}

@Composable
fun DetailBottomSheet(viewModel: DetailViewModel, movie: Movie?, onDismiss: () -> Unit) {
    // get movie counts for default lists from view model
    val planningCount by viewModel.planningCount.collectAsState()
    val watchingCount by viewModel.watchingCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    // check what lists the movie is already in
    val listsForMovie by viewModel.listsForMovie.collectAsState()
    var alreadyExistsInList: String? = null
    for (list in viewModel.defaultLists) {
        if (list.listName in listsForMovie) {
            alreadyExistsInList = list.listName
            break
        }
    }
    Column(modifier = Modifier.padding(1.dp)) {
        viewModel.defaultLists.forEach { defaultList ->
            // Only display "Completed", "Planning", and "Watching"
            //if (defaultList.listName in listOf("Planning", "Watching", "Completed") && defaultList.listName != alreadyExistsInList) {
            Row(
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
                    .clickable {
                        movie?.let {
                            if (alreadyExistsInList != null) { // if the movie is already in a default list, move it
                                viewModel.moveMovieToList(
                                    alreadyExistsInList,
                                    defaultList.listName,
                                    it
                                )
                            } else { // otherwise, add it to the selected default list
                                viewModel.addMovieToList(defaultList.listName, it)
                            }
                        }
                        onDismiss()
                    }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Choose icon based on singleList.listName
                val icon = when (defaultList.listName) {
                    "Completed" -> R.drawable.completed_icon
                    "Planning" -> R.drawable.planning_icon
                    "Watching" -> R.drawable.watching_icon
                    else -> R.drawable.custom_list // won't ever appear
                }
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = defaultList.listName,
                    modifier = Modifier.padding(start=8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = defaultList.listName,
                    fontWeight = if (defaultList.listName == alreadyExistsInList) FontWeight.ExtraBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = when (defaultList.listName) {
                        "Planning" -> planningCount.toString()
                        "Watching" -> watchingCount.toString()
                        "Completed" -> completedCount.toString()
                        else -> "0"
                    }, //
                    fontWeight = if (defaultList.listName == alreadyExistsInList) FontWeight.ExtraBold else FontWeight.Normal,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}