package com.example.inventory.ui.home

//import com.example.inventory.ui.theme.dark_highlight_med
//import com.example.inventory.ui.theme.dark_pine
import android.annotation.SuppressLint
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var movieToAdd by remember { mutableStateOf<Movie?>(null) } // Make this a state
    var userRating by rememberSaveable { mutableFloatStateOf(movieToAdd?.userRating ?: 0.0f) }
    val coroutineScope = rememberCoroutineScope()

    // Get collection ID within LaunchedEffect
    //var collectionID by remember { mutableStateOf<Int?>(null) }
    // Fetch collection ID *before* creating the ViewModel
//    LaunchedEffect(movieId) {
//        coroutineScope.launch(Dispatchers.IO) {
//            collectionID = getCollectionIdForMovie(movieId)
//        }
//    }

    // Get the application context (safe in Compose)
    val context = LocalContext.current.applicationContext as InventoryApplication
    val userListRepository = context.container.userListRepository
    val listMoviesRepository = context.container.listMoviesRepository
    val movieRepository = context.container.movieRepository

    // Use viewModel() with the factory
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(
            userListRepository,
            listMoviesRepository,
            movieRepository,
            movieId
            //collectionID ?: -1 // Provide a default value if collectionId is null
        )
    )

    // collect data from ListScreenViewModel
    val listsMovieIn by viewModel.listsForMovie.collectAsState()
    var expanded by remember { mutableStateOf(false) } // State for expanding synopsis
    val isInList by remember { derivedStateOf { "Planning" in listsMovieIn || "Watching" in listsMovieIn || "Completed" in listsMovieIn } }

    var showChangeRatingDialog by remember { mutableStateOf(false) }

    // for vibration feedback
    //val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    val cardColor = MaterialTheme.colorScheme.surfaceContainerHighest


    //Alter code below to fetch from local database instead of using the TMDB function
    LaunchedEffect(key1 = movieId) {
        coroutineScope.launch(Dispatchers.IO) {
            movieDetails = getDetailsFromID(movieId) // Fetch from API (if needed)

            movieRepository.getMovieStream(movieId).collect { movieFromDb ->
                movieToAdd = movieFromDb // Update the 'movie' state

                if (movieFromDb!= null) {
                    userRating = movieFromDb.userRating!! // Sync userRating from DB
                } else {
                    // Movie not in DB, create from API data (but don't set userRating yet)
                    movieDetails?.let { details ->
                        movieToAdd = Movie(
                            movieId,
                            details.title,
                            details.posterPath,
                            details.releaseDate,
                            details.runtime,
                            0.0f, // Default user rating
                            emptyList()
                        )
                    }
                }
            }
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
                }
            )
        },
        // Add movie to list FAB
        floatingActionButton = {
            movieToAdd?.let { movie -> // Use movie here for clarity
                // Animate the containerColor
                val animatedContainerColor by animateColorAsState(
                    targetValue = if (isInList) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    animationSpec = tween(durationMillis = 400) // Adjust duration as needed
                )

                FloatingActionButton(
                    onClick = {
                        if (isInList) {
                            // Delete the movie from whichever list it's in
                            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                            viewModel.deleteMovie(movieId) // New function in ViewModel

                        } else {
                            // Add the movie to the "Planning" list
                            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            viewModel.addMovieToList("Planning", movie)
                        }
                    },
                    containerColor = animatedContainerColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
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
                        val color = animateColorAsState(if (targetState) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary)
                        Icon(
                            imageVector = icon,
                            contentDescription = if (targetState) "Remove from List" else "Add to List",
                            tint = color.value
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
                        containerColor = cardColor,
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
                                    .clickable {/* Expand image to full size */ }
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
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            movieDetails?.let { it1 ->
                                Text(
                                    text = it1.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
                            Row {
                                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.runtime),
                                    contentDescription = "Runtime Icon",
                                    modifier = Modifier.size(20.dp) // Adjust size as needed
                                )
                                Spacer(modifier = Modifier.width(4.dp)) // Small spacing between icon and text
                                if (movieDetails?.runtime != 0)
                                {
                                    Text(
                                        text = formatRuntime(movieDetails?.runtime),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                } else {
                                    Text(
                                        text = "Unknown", // Fallback text
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing

                            Row {
                                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.release),
                                    contentDescription = "Release Icon",
                                    modifier = Modifier.size(20.dp) // Adjust size as needed
                                )
                                Spacer(modifier = Modifier.width(4.dp)) // Small spacing between icon and text
                                if (movieDetails?.releaseDate != null) {
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
                                            text = "Unknown", // Fallback text
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.audience),
                                    contentDescription = "Audience Icon",
                                    modifier = Modifier.size(20.dp) // Adjust size as needed
                                )
                                Spacer(modifier = Modifier.width(4.dp)) // Small spacing between icon and text
                                if (movieDetails?.audienceRating != 0.0)
                                {
                                    RatingText(movieDetails)
                                } else {
                                    Text(
                                        text = "N/A", // Fallback text
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                            }

                            Spacer(modifier = Modifier.height(18.dp))
                            AnimatedVisibility(
                                visible = isInList,
                                enter = scaleIn(tween(500)) + fadeIn(tween(500)), // Or other animations
                                exit = scaleOut(tween(500)) + fadeOut(tween(500))
                            ) {
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
                                            //.clip(CircleShape)
                                            //.size(100.dp)
                                            .clickable {
                                                showChangeRatingDialog = true
                                            } // display the dialog
                                    ) {
                                        RatingCircle(
                                            userRating = userRating, // Add ? before toFloat()
                                            fontSize = 28.sp,
                                            radius = 50.dp,
                                            animDuration = 1000,
                                            strokeWidth = 8.dp,
                                            isDetail = true
                                        )

                                    }
                                }
                            }
                            if (isInList){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Your Rating",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = isInList,
                    enter = slideInVertically(animationSpec = tween(durationMillis = 500)) + fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = slideOutVertically(animationSpec = tween(durationMillis = 500)) + fadeOut(animationSpec = tween(durationMillis = 500))
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
                        containerColor = cardColor,
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
                        containerColor = cardColor,
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
                                    .padding(horizontal = 14.dp),
                                contentPadding = PaddingValues(vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                if (viewModel.movieCollection.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp) // Consistent padding on all sides
                    ) {
                        Column { // Use a Column to structure the content
                            viewModel.movieCollectionName?.let { it1 ->
                                Text(
                                    text = it1,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 12.dp), // Consistent padding
                                    textAlign = TextAlign.Left,
                                    //fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold // Add FontWeight for emphasis
                                )
                            }
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp), // Consistent padding
                                contentPadding = PaddingValues(vertical = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    viewModel.movieCollection,
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

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp) // Consistent padding on all sides
                ) {
                    Column { // Use a Column to structure the content
                        Text(
                            text = "Recommended (BETA)",
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
    if (showChangeRatingDialog) { // show the dialog for changing a rating
//        var tempUserRating by remember { mutableFloatStateOf(movieToAdd?.userRating ?: 0.0f) } // Temporary state for the rating being edited
//        var errorMessage by remember { mutableStateOf("") } // if it's blank, then the user can submit their rating, otherwise no
        var rating by remember { mutableFloatStateOf(movieToAdd?.userRating ?: 0.0f) }
        var textFieldValue by remember { mutableStateOf("%.1f".format(rating)) } // Separate state for text field display
        var errorMessage by remember { mutableStateOf("") }
        var isFocused by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangeRatingDialog = false },
            title = {
                Text(
                    text = "Your Rating",
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        shape = RoundedCornerShape(36.dp),
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue // Update text field display immediately

                            if (newValue.matches(Regex("^(10([.,])?0*)|([0-9]([.,])?[0-9]*)$"))) { // Improved regex validation
                                val parsedValue = newValue.replace(',', '.').toFloatOrNull() // Handle commas as decimal separators too
                                if (parsedValue != null && parsedValue in 0.0f..10.0f) {
                                    rating = parsedValue
                                    errorMessage = ""
                                } else {
                                    errorMessage = "Invalid rating (Must be 0.0-10.0)"
                                }
                            } else {
                                errorMessage = "Invalid characters"
                            }

                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .width(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused && !isFocused) {
                                    textFieldValue = "" // Clear the text field when it gains focus
                                }
                                isFocused = focusState.isFocused
                            },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color.Red, modifier = Modifier.padding(top = 4.dp).align(Alignment.CenterHorizontally))
                    }

                    LineSlider(
                        value = rating, // Use the single 'rating' state
                        onValueChange = { newValue ->
                            rating = newValue
                            textFieldValue = "%.1f".format(newValue) // Update text field when slider changes
                            errorMessage = ""
                        },
                        valueRange = 0.0f..10.0f,
                        steps = 20
                    )
                }
            },
            confirmButton = {
                Text(
                    "Confirm",
                    modifier = Modifier
                        .clickable {
                            if (errorMessage.isEmpty()) { // if there isn't an error message, let the user submit their rating
                                movieToAdd?.movieID?.let {
                                    viewModel.changeMovieRating(it, rating) // Update the actual userRating
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
