package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inventory.R
import com.example.inventory.data.api.MovieSearchResult
import com.example.inventory.data.api.displayRandomMovie
import com.example.inventory.data.api.getGenreHardCode
import com.example.inventory.data.api.getMovieQuery
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SearchDestination : NavigationDestination {
    override val route = "search"
    override val titleRes = R.string.search_title
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState",
    "CoroutineCreationDuringComposition"
)
@Composable
fun SearchScreen(navController: NavHostController) {
    val viewModel: SearchScreenViewModel = viewModel()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var tempMovieList by rememberSaveable { mutableStateOf(mutableListOf<MovieSearchResult>()) }
    var searchSubmitted by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var randomizeGenre by remember { mutableStateOf(Pair("",1))}


    val searchBarPadding by animateDpAsState(
        targetValue = if (active) 0.dp else 24.dp,
        label = "Search bar padding"
    )

    val searchBarVerticalPadding by animateDpAsState(
        targetValue = if (active) 0.dp else 16.dp,
        label = "Search bar padding"
    )

    val keyboardController = LocalSoftwareKeyboardController.current

    // for randomize button
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold (
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = searchBarPadding)
                    .padding(vertical = searchBarVerticalPadding),
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    searchSubmitted = false
                },
                onSearch = {
                    keyboardController?.hide()
                    searchSubmitted = true // Set to true when search is submitted
                    coroutineScope.launch(Dispatchers.IO) {
                        //delay(200)
                        async {
                            tempMovieList = getMovieQuery(searchQuery)
                        }.await()
                    }
                },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = {
                    Text(text = "Search for a movie")
                },
                leadingIcon = {
                    if (active) {
                        Icon(
                            modifier = Modifier.clickable { searchQuery = ""; active = false },
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Icon"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                },
                trailingIcon = {
                    if (active) {
                        Icon(
                            modifier = Modifier.clickable {
                                if (searchQuery.isNotEmpty()) {
                                    searchQuery = ""  // Clear the search query first
                                } else {
                                    active = false  // Close the search bar if the query is already empty
                                }
                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon",
                        )
                    }
                    else {
                        Icon (
                            modifier = Modifier.clickable {
                                isSheetOpen = true
                            },
                            painter = painterResource(R.drawable.random),
                            contentDescription = "Randomize movie button"
                        )
                    }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), // Background color of the search bar
                )
            ) {
                if (searchQuery.isNotEmpty()) {
                    SearchRows(tempMovieList, navController, searchSubmitted)
                } else {
                    tempMovieList.clear()
                }
            }
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (active) 0.dp else innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                )
        ) {


            if (isSheetOpen) {
                ModalBottomSheet(
                    containerColor = MaterialTheme.colorScheme.surface,
                    sheetState = sheetState,
                    onDismissRequest = { isSheetOpen = false },
                ) {
                    val genreList = getGenreHardCode()
                    LazyVerticalGrid(columns = GridCells.Fixed(2))
                    {
                        var randMovieID = 0
                        items(genreList.size) { genre ->
                            ListItem(
                                modifier = Modifier.combinedClickable(
                                    onClick = { },
                                ).clickable {
                                    isSheetOpen = false
                                    randomizeGenre = genreList[genre]
                                    coroutineScope.launch(Dispatchers.IO) {
                                        async {
                                            randMovieID = displayRandomMovie(randomizeGenre) ?: 0
                                        }.await()
                                        withContext(Dispatchers.Main) {
                                            navigateToMovieDetails(navController, randMovieID)
                                        }
                                    }
                                },
                                headlineContent = {
                                    Text(
                                        text = genreList[genre].first,
                                        textAlign = TextAlign.Center, // Center the text
                                        modifier = Modifier.fillMaxWidth() // Make text fill the width
                                    )
                                }
                            )
                        }
                    }
                }
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
//                .padding(
//                    top = if (active) 0.dp else innerPadding.calculateTopPadding(),
//                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
//                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
//                    //bottom = innerPadding.calculateBottomPadding()
//                    // Exclude bottom padding
//                )
            ) {
                item {
                    // Below code for trending and theater carousels on search screen
                    Column (
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = "Trending",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 12.dp, start = 20.dp), // Add some top padding for spacing
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp
                        )

                        LazyRow (
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.trendingMovies, key = { movie -> movie.id }) { movie ->
                                MovieCard(movie = movie) { movieId ->
                                    navigateToMovieDetails(navController, movieId)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp)) // Add vertical spacing

                        Text(
                            text = "In Theaters",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 12.dp, start = 20.dp), // Add some top padding for spacing
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp
                        )

                        LazyRow (
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.nowPlayingMovies, key = { movie -> movie.id }) { movie ->
                                MovieCard(movie = movie) { movieId ->
                                    navigateToMovieDetails(navController, movieId)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp)) // Add vertical spacing

                        Text(
                            text = "Upcoming",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 12.dp, start = 20.dp), // Add some top padding for spacing
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp
                        )

                        LazyRow (
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.upcomingMovies, key = { movie -> movie.id }) { movie ->
                                MovieCard(movie = movie) { movieId ->
                                    navigateToMovieDetails(navController, movieId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Shows the search results as a vertical grid after entering query
@Composable
fun SearchRows(movieList: List<MovieSearchResult>, navController: NavHostController, searchSubmitted: Boolean) {
    if (movieList.isNotEmpty()) {
        LazyVerticalGrid(
            //columns = GridCells.Fixed(2),
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 8.dp, end = 8.dp) // Add bottom padding
            ) {
            items(movieList) { movie ->
                Column(
                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Center the text
                ) {
                    MovieCard(movie = movie) { movieId ->
                        navigateToMovieDetails(navController, movieId)
                    }
                    // Movie title
                    Text(
                        text = movie.title,
                        modifier = Modifier.padding(top = 8.dp), // Add some spacing between image and text
                        textAlign = TextAlign.Center // Center the text within its container
                    )
                }
            }
        }
    } else if (searchSubmitted) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No results found.")
        }
    }
}

// function that handles navController and passes movieId to detail screen
fun navigateToMovieDetails(navController: NavHostController, movieId: Int) {
    navController.navigate(DetailDestination.createRoute(movieId))
}