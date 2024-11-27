package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.data.movie.Movie
import com.example.inventory.data.userlist.UserList
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.dark_pine
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
    val userListRepository = InventoryApplication().container.userListRepository // use app container to get repository
    val listMoviesRepository = InventoryApplication().container.listMoviesRepository
    val movieRepository = InventoryApplication().container.movieRepository
    val viewModel: ListScreenViewModel = viewModel(factory = ListScreenViewModelFactory(userListRepository,
        listMoviesRepository,
        movieRepository)
    )
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showModal by remember { mutableStateOf(false) }

    // collect data from ListScreenViewModel
    val allLists by viewModel.allLists.collectAsState()
    val selectedList by viewModel.selectedList.collectAsState()
    val listMovies by viewModel.allMovies.collectAsState()

    // icons for changing list views
    val gridIcon = painterResource(id = R.drawable.grid_view)
    val horizontalIcon = painterResource(id = R.drawable.horizontal_view_icon)

    var listViewIcon by remember { mutableStateOf(horizontalIcon) } // either gonna be gridIcon or horizontalIcon (default)
    var showGridView by remember { mutableStateOf(true) } // need bool for switching icon

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) } // State to track search mode


    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp
                ),
                title = {
                    if (isSearching) { // Display TextField when searching
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            // Remove label and any other visual elements
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp), // Add some padding
                            singleLine = true, // Ensure single line input
                            textStyle = TextStyle(
                                color = Color.White, // Set text color to white
                                fontSize = 18.sp // Adjust font size as needed
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
                actions = {
                    IconButton(onClick = {
                        isSearching = !isSearching // Toggle search mode
                        if (!isSearching) {
                            searchQuery = "" // Clear search query when exiting search mode
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(onClick = {/* TODO: Some shit idk yet */}) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Triple dot menu"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton( // opens up the bottom sheet for list selection and editing
                onClick = {
                    coroutineScope.launch {
                        showModal = true // open the bottom sheet
                    }
                },
                icon = {
                    // Choose icon based on selectedList
                    // As the code currently is, if a user makes a custom list, the FAB icon
                    // will be the same as the All icon instead of the custom icon in the bottom sheet
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
                text = { Text(text = if (selectedList == "") "All" else selectedList,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis) }, // default
                containerColor = dark_pine,
                contentColor = Color.White,
                modifier = Modifier.sizeIn(maxWidth = 150.dp)
            )
        }
    ) { //innerPadding ->
        Column {
        Row( // contains sorting and view selection buttons
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        /*TODO: Sorting*/
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.padding(start = 6.dp, bottom = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sort_icon),
                        contentDescription = "Sorting",
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = "Sort"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            showGridView = !showGridView // helps switch the view
                            listViewIcon = if (showGridView) horizontalIcon else gridIcon // changes icon
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.padding(end = 6.dp, bottom = 12.dp)
                ) {
                    Icon(painter = listViewIcon, contentDescription = "View")
                }
            }

            // Filtered movie list
            val filteredMovies = if (searchQuery.isEmpty()) {
                listMovies
            } else {
                listMovies.filter { movie ->
                    movie.title.contains(searchQuery, ignoreCase = true)
                }
            }

            // display movies based on view selection (default grid view)
            if (showGridView)
                ListGridView(navController, filteredMovies, selectedList, searchQuery, navbarModifier = modifier)
            else
                ListHorizontalView(navController, filteredMovies, selectedList, searchQuery, navbarModifier = modifier)
        }
    }
    // bottom sheet displays after clicking FAB
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState,
        ) {
            ListSelectBottomSheet(allLists, viewModel, selectedList) { showModal = false }
        }
    }
}

@Composable
fun ListGridView(
    navController: NavHostController,
    listMovies: List<Movie>,
    currList: String,
    searchQuery: String,
    navbarModifier: Modifier = Modifier //for navbar height adjustment
) {
    val filteredMovies = if (searchQuery.isEmpty()) {
        listMovies
    } else {
        listMovies.filter { movie ->
            movie.title.contains(searchQuery, ignoreCase = true)
        }
    }
    // grid layout for movies, showing only poster and title
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = navbarModifier.fillMaxSize(),
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
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                navigateToLocalDetails(navController, movie.movieID, currList)
                            }
                            .width(135.dp)
                            .aspectRatio(0.6667f),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.padding(2.dp)) // some space between poster and title
                Text( // display the title
                    text = movie.title,
                    fontSize = 14.sp,
                    lineHeight = 1.5.em,
                    modifier = Modifier.width(135.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ListHorizontalView(
    navController: NavHostController,
    listMovies: List<Movie>,
    currList: String,
    searchQuery: String,
    navbarModifier: Modifier = Modifier
) {
    val filteredMovies = if (searchQuery.isEmpty()) {
        listMovies
    } else {
        listMovies.filter { movie ->
            movie.title.contains(searchQuery, ignoreCase = true)
        }
    }
    LazyColumn(
        modifier = navbarModifier
            .fillMaxSize()
    ) {
        items(filteredMovies) { movie ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .clickable {
                        navigateToLocalDetails(navController, movie.movieID, currList)
                    }
            ) {
                Card(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
                            .aspectRatio(0.6667f),
                        contentScale = ContentScale.Crop
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
                            fontSize = 20.sp)
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
                            text = "${movie.runtime} mins",
                            fontSize = 13.sp,
                            lineHeight = 1.5.em,
                            modifier = Modifier.padding(top = 5.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row {
                        Text(
                            text = "Your Rating: %.1f / 10".format(movie.userRating),
                            fontSize = 13.sp,
                            lineHeight = 1.5.em,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

@Composable
fun ListSelectBottomSheet(allLists: List<UserList>, viewModel: ListScreenViewModel, currList: String, onDismiss: () -> Unit) {
    // these are used for the rename list dialog
    var showRenameDialog by remember { mutableStateOf(false) }
    var oldListName by remember { mutableStateOf("") }

    val planningCount by viewModel.planningCount.collectAsState()
    val watchingCount by viewModel.watchingCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.5f).dp) // Set the height to half the screen
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp, end = 2.dp, bottom = 100.dp)
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
            item {
                HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp))
            }
            // now display lists stored in the DB
            items(allLists) { singleList ->
                if (singleList.listName !in listOf("Completed", "Planning", "Watching")) {
                    var expanded by remember { mutableStateOf(false) } // State for dropdown menu
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectList(singleList.listName)
                                viewModel.updateListMovies(singleList.listName)
                                onDismiss()
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.custom_list), // custom icon when user makes list
                            contentDescription = singleList.listName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = singleList.listName,
                            fontWeight = if (singleList.listName == currList) FontWeight.ExtraBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = singleList.movieCount.toString(),
                            fontWeight = if (singleList.listName == currList) FontWeight.ExtraBold else FontWeight.Normal,
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                            textAlign = TextAlign.Right
                        )
                        Box {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                modifier = Modifier
                                    .clickable {
                                        expanded = true
                                    }
                            )
                            // Dropdown menu for MoreVert icon
                            DropdownMenu(
                                shape = RoundedCornerShape(18.dp),
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                }, // Close the menu when clicked outside
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "Rename") },
                                    onClick = {
                                        showRenameDialog =
                                            true // show the rename list dialog
                                        oldListName = singleList.listName
                                        expanded = false // Close the menu
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "Delete", color = Color.Red) },
                                    onClick = {
                                        viewModel.deleteList(singleList.listName) // delete the list
                                        viewModel.selectList("") // reset back to default "All"
                                        viewModel.updateListMovies("")
                                        expanded = false // Close the menu
                                    }
                                )

                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
                .padding(bottom = 30.dp)
        ) {
            // button for creating a new list
            AddNewListButtonWithDialog(viewModel)
        }
    }
    // dialog for renaming a list
    if (showRenameDialog) {
        var newListName by remember { mutableStateOf(oldListName) }
        var listExistsError by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRenameDialog = false},
            title = { Text(text = "Rename") },
            text = {
                Column {
                    Text(text = "Give your list a new name:")
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        shape = RoundedCornerShape(10.dp),
                        value = newListName,
                        onValueChange = {
                            newListName = it
                            viewModel.newListNameExists(oldListName, newListName.trim())
                            listExistsError = if (viewModel.isInList) { // if new name already exists, display error message
                                "A list of that name already exists!"
                            } else {
                                ""
                            }
                        },
                        label = {
                            Text(
                                "New Name",
                                color = LocalContentColor.current.copy(alpha = 0.5f) // makes text more transparent
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(text = listExistsError, color = Color.Red, modifier = Modifier.padding(start = 10.dp))

                }
            },
            confirmButton = {
                Text(
                    "Rename",
                    modifier = Modifier
                        .clickable {
                            viewModel.newListNameExists(oldListName, newListName.trim())
                            if (!viewModel.isInList && newListName.isNotBlank()) { // if new name doesn't already exist
                                viewModel.renameList(oldListName, newListName.trim())
                                // need to select list again
                                if (currList == oldListName) {
                                    viewModel.selectList(newListName.trim())
                                    viewModel.updateListMovies(newListName.trim())
                                }
                                showRenameDialog = false
                            } else if (newListName.isBlank()) { // display message if whitespace is entered
                                listExistsError = "List name can't be blank!"
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
                            showRenameDialog = false
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            }
        )
    }
}

@Composable
fun AddNewListButtonWithDialog(viewModel: ListScreenViewModel) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var listName by remember { mutableStateOf("") }
    var listExistsError by remember { mutableStateOf("") }

    // "add custom list" button
    Row(
        modifier = Modifier
            .padding(start = 2.dp, end = 2.dp)
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f)) //pushes button to center
        SmallFloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = dark_pine,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create new list",
            )
        }
        Spacer(modifier = Modifier.weight(1f)) //fills remaining space
    }
    // the dialog for creating a new custom list
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(text = "Create a List") },
            text = {
                Column {
                    Text(text = "Enter a name for your new list:")
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        shape = RoundedCornerShape(10.dp),
                        value = listName,
                        onValueChange = {
                            listName = it
                            viewModel.newListNameExists(null, listName.trim())
                            listExistsError = if (viewModel.isInList) { // if name already exists, display error message
                                "A list of that name already exists!"
                            } else {
                                ""
                            }
                        },
                        label = {
                            Text(
                                "Name",
                                color = LocalContentColor.current.copy(alpha = 0.5f) // makes text more transparent
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(text = listExistsError, color = Color.Red, modifier = Modifier.padding(start = 10.dp, top = 5.dp))
                }
            },
            confirmButton = {
                Text(
                    "Create",
                    modifier = Modifier
                        .clickable {
                            viewModel.newListNameExists(null, listName.trim())
                            if (!viewModel.isInList && listName.isNotBlank()) { // if list doesn't already exist
                                viewModel.addNewList(listName.trim()) // insert list into db, trimming leading and trailing whitespace
                                showCreateDialog = false
                                listName = "" // reset list name
                            } else if (listName.isBlank()) { // display error if attempting to submit whitespace
                                listExistsError = "List name can't be blank!"
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
                            showCreateDialog = false
                            listName = ""
                            listExistsError = ""
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            }
        )
    }
}

// function that handles navController and passes movieId to detail screen
fun navigateToLocalDetails(navController: NavHostController, movieId: Int, currList: String) {
    navController.navigate(LocalDetailDestination.createRoute(movieId, currList))
}