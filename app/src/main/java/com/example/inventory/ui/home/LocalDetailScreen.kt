package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.data.movie.Movie
import com.example.inventory.data.userlist.UserList
import com.example.inventory.ui.theme.dark_highlight_med
import com.example.inventory.ui.theme.material_green
import com.example.inventory.ui.theme.material_orange
import com.example.inventory.ui.theme.material_red
import com.example.inventory.ui.theme.material_yellow
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object LocalDetailDestination {
    const val ROUTE = "localMovieDetails/{movieId}/{currList}"

    fun createRoute(movieId: Int, currList: String): String {
        return "localMovieDetails/$movieId/$currList"
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
        movieRepository,
        movieId)
    )
    var showChangeRatingDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showModal by remember { mutableStateOf(false) } // for popping up bottom sheet
    var fabClicked by remember { mutableStateOf(true) } // if copy icon clicked, this is false
    var expanded by remember { mutableStateOf(false) } // State for expanding synopsis

    val allLists by viewModel.allLists.collectAsState()


// Fetch movie details from the local database using movieId
    LaunchedEffect(key1 = movieId) {
        movieRepository.getMovieStream(movieId).collect {
            movie = it
        }
    }
    Scaffold(
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
                            text = it.title,
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
                            navController.popBackStack()
                            viewModel.deleteMovie(movieId)
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
        },
    ) {
        LazyColumn {
            item {
                // Image and text in a Row
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
                    Box {
                        val icon = painterResource(id = R.drawable.custom_list)
                        Icon(
                            painter = icon,
                            contentDescription = "Copy to list",
                            modifier = Modifier
                                .padding(end = 10.dp, bottom = 10.dp)
                                .align(Alignment.BottomEnd) // puts the icon in top right corner of card
                                .clickable {
                                    showModal = true
                                    fabClicked = false
                                    // TODO: if there are no custom lists, popup create list dialog (copy from list screen), and then add to that list
                                }
                        )
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
                                    error = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(0.6667f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Image not available")
                                        }
                                    }
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
                                        text = "Your Rating",
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
                                            //.clip(CircleShape)
                                            //.size(100.dp)
                                            .clickable {
                                                showChangeRatingDialog = true
                                            } // display the dialog
                                    ) {
                                        RatingCircle(userRating = movie?.userRating ?: 0.0f, fontSize = 28.sp, radius = 50.dp, animDuration = 1000, strokeWidth = 8.dp)
                                    }
                                    //Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            item {
                StatusButtons(viewModel)
                Spacer(modifier = Modifier.height(16.dp))
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
                                    text = it1.overview ?: "",
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
                                        text = if ((it1.overview?.length ?: 0) > 250) "${it1.overview?.substring(0, 250)}..." else it1.overview ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
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
            title = {
                Text(
                    text = "Your Rating",
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    // Editable text field, border is dark blue unfocused and becomes brighter when user clicks on it
                    OutlinedTextField(
                        shape = RoundedCornerShape(36.dp),
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
                            unfocusedBorderColor = dark_highlight_med,
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
                    //Spacer(modifier = Modifier.height(15.dp))
                    // Slider for changing rating value from 0.0 to 10.0
                    LineSlider(
                        value = if (newRating.toFloatOrNull() != null && newRating.toFloat() in 0.0..10.0) newRating.toFloat() else 0.0f,
                        onValueChange = { value ->
                            errorMessage = "" // clear error message since slider will always have valid input
                            newRating = "%.1f".format(value) // format to tens place
                        },
                        valueRange = 0.0f..10.0f,
                        steps = 20, // 10.0 - 0.0 divided by 0.1 gives 100 steps

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
    if (showModal) { // popup bottom sheet
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState,
        ) {
            LocalDetailBottomSheet(allLists, viewModel, fabClicked) { showModal = false }
        }
    }
}

@Composable
fun RatingCircle(
    userRating: Float, // Now takes userRating directly
    fontSize: TextUnit,
    radius: Dp,
    // color: Color = MaterialTheme.colorScheme.outline,
    strokeWidth: Dp,
    animDuration: Int,
    animDelay: Int = 100
) {
    var animationPlayed by remember { mutableStateOf(false) }

    // Calculate percentage based on userRating (0 to 10 scale)
    val percentage = userRating / 10f

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        label = "Rating Animation",
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )
    LaunchedEffect(key1 = true) { animationPlayed = true }

    // Determine the color based on userRating
    // Use animateColorAsState for smooth color transitions
    val color = animateColorAsState(
        targetValue = when (userRating) {
            in 0.0f..2.9f -> material_red        // 0 - 2.9: Red
            in 3.0f..4.9f -> material_orange     // 3 - 4.9: Orange
            in 5.0f..6.9f -> material_yellow     // 5 - 6.9: Yellow
            in 7.0f..8.9f -> material_green      // 7 - 8.9: Green
            in 9.0f..10.0f -> MaterialTheme.colorScheme.outline // 9 - 10: Blue
            else -> MaterialTheme.colorScheme.outline // Default color
        },
        label = "Color Animation" // Add a label for debugging
    ).value

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Canvas(
            modifier = Modifier.size(radius * 2f)
                //.clip(CircleShape)
        ) {
            // Draw the white circle first
            drawCircle(
                color = Color.White,
                radius = radius.toPx(), // Adjust radius for the stroke width
                style = Stroke(1.dp.toPx()) // Thin stroke width
            )

            // Draw arc on top
            drawArc(
                color = color,
                -90f,
                360 * curPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = String.format(java.util.Locale.ENGLISH, "%.1f", userRating), // Display userRating with one decimal place
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
// functionality for bottom sheet, content differs depending on if the FAB was clicked or the copy icon
@Composable
fun LocalDetailBottomSheet(allLists: List<UserList>, viewModel: LocalDetailViewModel, fabWasClicked: Boolean, onDismiss: () -> Unit) {
    // first determine the status of the movie
    val listsForMovie by viewModel.listsForMovie.collectAsState()
    var alreadyExistsInList: String? = null
    for (list in viewModel.defaultLists) {
        if (list.listName in listsForMovie) {
            alreadyExistsInList = list.listName
            break
        }
    }
    Column(modifier = Modifier.padding(1.dp)) {
        if (fabWasClicked) { // FAB was clicked so display default lists (statuses)
            Row(
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Currently in \"$alreadyExistsInList\"",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            viewModel.defaultLists.forEach { defaultList ->
                // Only display "Completed", "Planning", and "Watching"
                if (defaultList.listName in listOf(
                        "Completed",
                        "Planning",
                        "Watching"
                    ) && defaultList.listName != alreadyExistsInList
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .fillMaxWidth()
                            .clickable {
                                if (alreadyExistsInList != null) {
                                    viewModel.moveMovieToList(
                                        alreadyExistsInList,
                                        defaultList.listName
                                    )
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
                            else -> R.drawable.custom_list // custom icon when user makes list
                        }

                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = defaultList.listName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = defaultList.listName,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else { // copy icon was clicked, display user-created custom lists
            Row(
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Copy To", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Spacer(modifier = Modifier.weight(1f))
            }
            allLists.forEach { list ->
                // Only display lists that are NOT completed, planning, watching
                if (list.listName !in listOf("Completed", "Planning", "Watching")) {
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.addMovieToList(list.listName)
                                onDismiss()
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Choose icon based on singleList.listName
                        val icon = when (list.listName) {
                            "Completed" -> R.drawable.completed_icon
                            "Planning" -> R.drawable.planning_icon
                            "Watching" -> R.drawable.watching_icon
                            else -> R.drawable.custom_list // custom icon when user makes list
                        }

                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = list.listName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = list.listName,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SegmentedButtons(
    viewModel: LocalDetailViewModel,
    modifier: Modifier = Modifier
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
                    }
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
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val iconTint = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
            //.padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon), // Use painterResource
                contentDescription = null,
                tint = iconTint
            )
        }
    }
}

@Composable
fun StatusButtons(
    viewModel: LocalDetailViewModel, // Add the ViewModel
) {
    Column {
        SegmentedButtons(
            viewModel = viewModel
        )
    }
}