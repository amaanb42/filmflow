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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.data.api.MovieDetails
import com.example.inventory.data.api.getDetailsFromID
import com.example.inventory.data.userlist.UserList
import com.example.inventory.ui.theme.dark_pine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object DetailDestination {
    const val ROUTE = "movieDetails/{movieId}"

    fun createRoute(movieId: Int): String {
        return "movieDetails/$movieId"
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieDetailsScreen(navController: NavHostController, movieId: Int) {
    var movie by remember { mutableStateOf<MovieDetails?>(null) }
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
    val currList = selectedList?.listName // used for highlighting selection in bottom sheet

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
                    val icon = when (selectedList?.listName) {
                        "Completed" -> painterResource(id = R.drawable.completed_icon)
                        "Planning" -> painterResource(id = R.drawable.planning_icon)
                        "Watching" -> painterResource(id = R.drawable.watching_icon)
                        else -> painterResource(id = R.drawable.add_icon) // Default icon
                    }

                    Icon(
                        painter = icon,
                        contentDescription = "Add movie to list"
                    )
                },
                text = { Text(selectedList?.listName ?: "Add") },
                containerColor = dark_pine,
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
                // Card with movie art image
                Card(
                    modifier = Modifier
                        .padding(top = 120.dp)
                        .padding(start = 30.dp)
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
                        // Movie title
                        Text(
                            text = it1.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        text = (movie?.runtime?.toString() ?: "") + " minutes", // Convert to String or use empty string if null
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = (movie?.rating?.toString() ?: "") + "/10", // Convert to String or use empty string if null
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        //text = "Released: ${movie?.releaseDate}",
                        text = (movie?.releaseDate ?: ""),
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
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState,
        ) {
            ListSelectBottomSheet(allLists, viewModel, currList) { showModal = false }
        }
    }
}

@Composable
fun DetailBottomSheet(allLists: List<UserList>, viewModel: ListScreenViewModel, currList: String?, onDismiss: () -> Unit) {
    // these are used for the rename list dialog
    var showRenameDialog by remember { mutableStateOf(false) }
    var oldListName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(1.dp),

        ) {
//        HorizontalDivider(
//            modifier = Modifier.padding(start=20.dp, end=20.dp, top=5.dp, bottom=5.dp)
//        )
        // first item in list is always All, but in settings screen add option to change default list displayed
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .fillMaxWidth()
                .clickable {
                    viewModel.selectList(null)
                    onDismiss()
                }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.all_icon), // Or any other suitable icon
                contentDescription = "All",
                modifier = Modifier.padding(start=8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "All",
                fontWeight = if (currList == null) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
        }
        // separate default lists and user-created lists, so that all user-created lists appear after defaults
        // display default lists first
        viewModel.defaultLists.forEach { defaultList ->
            Row(
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
                    .clickable {
                        viewModel.selectList(defaultList)
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
                    modifier = Modifier.padding(start=8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = defaultList.listName,
                    fontWeight = if (defaultList.listName == currList) FontWeight.ExtraBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}