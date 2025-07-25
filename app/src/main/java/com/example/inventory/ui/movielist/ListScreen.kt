package com.example.inventory.ui.movielist

//import com.example.inventory.ui.theme.dark_pine
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.data.movie.Movie
import com.example.inventory.ui.composables.RatingCircle
import com.example.inventory.ui.composables.formatRuntime
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ListDestination : NavigationDestination {
    override val route = "list"
    override val titleRes = R.string.list_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(navController: NavHostController, modifier: Modifier = Modifier){

    // for list selection sheet
    // Get the application context (safe in Compose)
    val context = LocalContext.current.applicationContext as InventoryApplication
    val userListRepository = context.container.userListRepository
    val listMoviesRepository = context.container.listMoviesRepository
    val movieRepository = context.container.movieRepository

    // Use viewModel() with the factory
    val viewModel: ListScreenViewModel = viewModel(
        factory = ListScreenViewModelFactory(userListRepository, listMoviesRepository, movieRepository)
    )

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showModal by remember { mutableStateOf(false) }

    // variables to assist with hiding the FAB on scroll
    var fabVisible by rememberSaveable { mutableStateOf(true) }

    // collect data from ListScreenViewModel
    val selectedList by viewModel.selectedList.collectAsState()
    val listMovies by viewModel.allMovies.collectAsState()

    // icons for changing list views
    val gridIcon = painterResource(id = R.drawable.grid_view)
    val horizontalIcon = painterResource(id = R.drawable.horizontal_view_icon)
    
    val showGridView by viewModel.showGridView.collectAsState() // need bool for switching icon

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) } // State to track search mode
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester()}

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior() // topappbar hides/shows on scroll
    var expanded by remember { mutableStateOf(false) } // for drop-down menu

    var sortSelection by rememberSaveable { mutableStateOf("Title") } // Defaults to title sort

    // Create a sorted copy of the list
    val sortedMovies = when (sortSelection) {
        "Title" -> listMovies.sortedBy { it.title }
        "Rating" -> listMovies.sortedByDescending { it.userRating ?: 0.0f }
        "Release" -> listMovies.sortedByDescending { LocalDate.parse(it.releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
        "Runtime" -> listMovies.sortedBy { it.runtime }
        else -> listMovies
    }

    Scaffold(
        topBar = {
            TopAppBar(
//                windowInsets = WindowInsets(
//                    top = 0.dp,
//                    bottom = 0.dp
//                ),
                title = {
                    if (isSearching) { // Display TextField when searching
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            // Remove label and any other visual elements
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            singleLine = true, // Ensure single line input
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent, // Hide border
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    } else { // Display list title when not searching
                        Text(
                            text = if (selectedList == "") "Movie List" else selectedList,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                //colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        isSearching = !isSearching // Toggle search mode
                    }) {
                        Icon(
                            imageVector = if (isSearching) Icons.Filled.Close else Icons.Filled.Search,
                            contentDescription = if (isSearching) "Close Search" else "Search"
                        )
                    }

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            painter = painterResource(id = R.drawable.sort_icon),
                            contentDescription = "Sorting",
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // First section
                        DropdownMenuItem(
                            text = { Text("Title") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null) },
                            onClick = { sortSelection = "Title" }
                        )
                        DropdownMenuItem(
                            text = { Text("Rating") },
                            leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null) },
                            onClick = { sortSelection = "Rating" }
                        )
                        DropdownMenuItem(
                            text = { Text("Release") },
                            leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null) },
                            onClick = { sortSelection = "Release" }
                        )
                        DropdownMenuItem(
                            text = { Text("Runtime") },
                            leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null) },
                            onClick = { sortSelection = "Runtime" }
                        )
                    }

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.changeListView() // helps switch the view
                            }
                        },
                        //colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        //modifier = Modifier.padding(end = 6.dp, bottom = 12.dp)
                    ) {
                        Icon(painter = if (showGridView) horizontalIcon else gridIcon, contentDescription = "View")
                    }

//                    IconButton(onClick = {/* TODO: Some shit idk yet */}) {
//                        Icon(
//                            imageVector = Icons.Filled.MoreVert,
//                            contentDescription = "Triple dot menu"
//                        )
//                    }
                },
            )
            //LaunchedEffect for focus requesting in outlined text field for searching
            LaunchedEffect(isSearching) {
                if (isSearching) {
                    keyboardController?.show()
                    focusRequester.requestFocus()
                } else {
                    searchQuery = ""
                    keyboardController?.hide()
                }
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                ExtendedFloatingActionButton( // opens up the bottom sheet for list selection and editing
                    onClick = {
                        coroutineScope.launch {
                            showModal = true // open the bottom sheet
                        }
                    },
                    icon = {
                        // Choose icon based on selectedList
                        val icon = when (selectedList) {
                            "Completed" -> painterResource(id = R.drawable.completed_icon)
                            "Planning" -> painterResource(id = R.drawable.planning_icon)
                            "Watching" -> painterResource(id = R.drawable.watching_icon)
                            else -> painterResource(id = R.drawable.all_icon) // Default icon
                        }

                        Icon(
                            painter = icon,
                            contentDescription = "Edit"
                        )
                    },
                    text = {
                        Text(
                            text = if (selectedList == "") "All" else selectedList,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }, // default
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.sizeIn(maxWidth = 300.dp).offset(y = (20).dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                ) // Exclude bottom padding
                .fillMaxSize()
        ) {
            // Filtered movie list
            if (searchQuery.isNotEmpty()) {
                sortedMovies.filter { movie ->
                    movie.title.contains(searchQuery, ignoreCase = true)
                }
            }

            // display movies based on view selection (default grid view)
            if (showGridView) {
                ListGridView(
                    navController,
                    sortedMovies,
                    searchQuery,
                    // Pass fabVisible
                    { newValue -> fabVisible = newValue }, // Pass a lambda to update fabVisible
                    modifier = modifier
                )
            } else {
                ListHorizontalView(
                    navController,
                    sortedMovies,
                    searchQuery,
                    // Pass fabVisible
                    { newValue -> fabVisible = newValue }, // Pass a lambda to update fabVisible
                    modifier = modifier
                )
            }
        }
    }
    // bottom sheet displays after clicking FAB
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState,
        ) {
            ListSelectBottomSheet(viewModel, selectedList) { showModal = false }
        }
    }
}

@Composable
fun ListGridView(
    navController: NavHostController,
    listMovies: List<Movie>,
    searchQuery: String,
    // Receive fabVisible
    updateFabVisible: (Boolean) -> Unit, // Receive the update lambda
    modifier: Modifier = Modifier // for navbar height adjustment
) {
    val filteredMovies = if (searchQuery.isEmpty()) {
        listMovies
    } else {
        listMovies.filter { movie ->
            movie.title.contains(searchQuery, ignoreCase = true)
        }
    }

    var lastScrollIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberLazyGridState()

    // grid layout for movies, showing only poster and title
    LazyVerticalGrid(
        state = scrollState,
        //columns = GridCells.Fixed(3),
        columns = GridCells.Adaptive(minSize = 108.dp),
        modifier = modifier.fillMaxSize(),
            //.padding(top = 36.dp),
        contentPadding = PaddingValues(horizontal = 15.dp)
    ) { // display the movies
        items(filteredMovies) { movie ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    //.padding(10.dp)
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                Card { // display the poster
                    SubcomposeAsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                navigateToLocalDetails(navController, movie.movieID)
                            }
                            //width(135.dp)
                            .fillMaxWidth()
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
                Spacer(modifier = Modifier.padding(2.dp)) // some space between poster and title
                Text( // display the title
                    text = movie.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 1.5.em,
                    modifier = Modifier.width(135.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    LaunchedEffect(scrollState) {
        var isFirstEmission = true
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .collect { index ->
                if (isFirstEmission) {
                    isFirstEmission = false
                    updateFabVisible(true) // Ensure FAB is visible initially
                } else {
                    updateFabVisible(index < lastScrollIndex) // Only hide when scrolling down
                }
                lastScrollIndex = index
            }
    }
}

@Composable
fun ListHorizontalView(
    navController: NavHostController,
    listMovies: List<Movie>,
    searchQuery: String,
    // Receive fabVisible
    updateFabVisible: (Boolean) -> Unit, // Receive the update lambda
    modifier: Modifier = Modifier // for navbar height adjustment
) {
    val filteredMovies = if (searchQuery.isEmpty()) {
        listMovies
    } else {
        listMovies.filter { movie ->
            movie.title.contains(searchQuery, ignoreCase = true)
        }
    }

    var lastScrollIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
    ) {
        items(filteredMovies) { movie ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .clickable {
                        navigateToLocalDetails(navController, movie.movieID)
                    }
            ) {
                Card(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    SubcomposeAsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
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
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp),
                ) {
                    Row {
                        Text(text = movie.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false,
                            fontSize = 18.sp)
                    }
                    Row {
                        val originalDate = LocalDate.parse(movie.releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val formattedDate = originalDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        Text(
                            text = "Release: ",
                            fontSize = 13.sp,
                            lineHeight = 1.5.em,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 13.sp,
                            lineHeight = 1.5.em,
                            modifier = Modifier.padding(top = 5.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row {
                        Text(
                            text = "Runtime: ",
                            fontSize = 13.sp,
                            lineHeight = 1.5.em,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        Text(
                            text = formatRuntime(movie.runtime),
                            fontSize = 13.sp,
                            lineHeight = 1.5.em,
                            modifier = Modifier.padding(top = 5.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        //Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            Modifier
                                .align(Alignment.CenterVertically)
                                //.padding(top = 24.dp)
                                .padding(top = 16.dp, start = 4.dp)
                        ) {
                            RatingCircle(userRating = movie.userRating ?: 0.0f, fontSize = 16.sp, radius = 24.dp, animDuration = 0, strokeWidth = 4.dp, isDetail = false)
                        }
                        //Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(all = 8.dp).padding(start = 4.dp, end = 4.dp))
        }
    }
    LaunchedEffect(scrollState) {
        var isFirstEmission = true
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .collect { index ->
                if (isFirstEmission) {
                    isFirstEmission = false
                    updateFabVisible(true) // Ensure FAB is visible initially
                } else {
                    updateFabVisible(index < lastScrollIndex) // Only hide when scrolling down
                }
                lastScrollIndex = index
            }
    }
}

@Composable
fun ListSelectBottomSheet(viewModel: ListScreenViewModel, currList: String, onDismiss: () -> Unit) {
    // these are used for the rename list dialog

    val planningCount by viewModel.planningCount.collectAsState()
    val watchingCount by viewModel.watchingCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(start = 2.dp, end = 2.dp, bottom = 100.dp) //bottom padding to make room for custom lists
                .padding(start = 2.dp, end = 2.dp)
        ) {
            // first item in list is always All, but in settings screen add option to change default list displayed
            item {
                Row(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectList("")
                            viewModel.updateListMovies("")
                            onDismiss()
                        }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.all_icon), // Or any other suitable icon
                        contentDescription = "All",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "All",
                        fontWeight = if (currList == "") FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = (planningCount + watchingCount + completedCount).toString(),
                        fontWeight = if (currList == "") FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        textAlign = TextAlign.Right
                    )
                }
            }
            // separate default lists and user-created lists, so that all user-created lists appear after defaults
            // display default lists first
            items(viewModel.defaultLists) { defaultList ->
                Row(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectList(defaultList.listName)
                            viewModel.updateListMovies(defaultList.listName)
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
                        fontWeight = if (defaultList.listName == currList) FontWeight.ExtraBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = when (defaultList.listName) {
                            "Planning" -> planningCount.toString()
                            "Watching" -> watchingCount.toString()
                            "Completed" -> completedCount.toString()
                            else -> "0"
                        }, //
                        fontWeight = if (defaultList.listName == currList) FontWeight.ExtraBold else FontWeight.Normal,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        textAlign = TextAlign.Right
                    )
                }
            }
        }
    }
}

// function that handles navController and passes movieId to detail screen
fun navigateToLocalDetails(navController: NavHostController, movieId: Int) {
    navController.navigate(DetailDestination.createRoute(movieId))
}