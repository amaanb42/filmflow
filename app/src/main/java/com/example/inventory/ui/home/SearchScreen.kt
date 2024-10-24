package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.R
import com.example.inventory.data.api.MovieSearchResult
import com.example.inventory.data.api.displayRandomMovie
import com.example.inventory.data.api.getGenre
import com.example.inventory.data.api.getMovieQuery
import com.example.inventory.data.api.getNowPlayingMovies
import com.example.inventory.data.api.getTrendingMovies
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var tempMovieList by remember { mutableStateOf(mutableListOf<MovieSearchResult>()) }
    var trendingMovies by remember { mutableStateOf(listOf<MovieSearchResult>())}
    val coroutineScope = rememberCoroutineScope()
    var genreList by remember { mutableStateOf(listOf<Pair<String, Int>>()) }
    var randomizeGenre by remember { mutableStateOf(Pair("",1))}
    var nowPlayingMovies by remember { mutableStateOf(listOf<MovieSearchResult>())}


    val searchBarPadding by animateDpAsState(
        targetValue = if (active) 0.dp else 24.dp,
        label = "Search bar padding"
    )

    val searchBarVerticalPadding by animateDpAsState(
        targetValue = if (active) 0.dp else 16.dp,
        label = "Search bar padding"
    )

    val keyboardController = LocalSoftwareKeyboardController.current

    //Need Coroutine to do any API searches
    coroutineScope.launch(Dispatchers.IO) {
        async {
            trendingMovies = getTrendingMovies()
            nowPlayingMovies = getNowPlayingMovies()
        }.await()
    }

    Scaffold { padding -> // Removed topBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = searchBarPadding)
                    .padding(vertical = searchBarVerticalPadding),
                query = text,
                onQueryChange = {
                    text = it
                },
                onSearch = {
                    keyboardController?.hide()

                    coroutineScope.launch(Dispatchers.IO) {
                        delay(200)
                        async {
                            tempMovieList = getMovieQuery(text)
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
                            modifier = Modifier.clickable { text = ""; active = false },
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
                            modifier = Modifier.clickable { text = "" },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon",
                        )
                    }
                }
            ) {
                if (text.isNotEmpty()) {
                    SearchRows(tempMovieList, navController)
                } else {
                    tempMovieList.clear()
                }
            }

            val sheetState = rememberModalBottomSheetState()
            var isSheetOpen by rememberSaveable {
                mutableStateOf(false)
            }
            // Randomize button
            Button(
                onClick = {
                    isSheetOpen = true
                },
                //enabled = itemUiState.isEntryValid,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 145.dp)
                    .padding(top = 0.dp),
            ) {
                Text(text = stringResource(R.string.random_button))
            }

            if (isSheetOpen) {
                ModalBottomSheet(
                    containerColor = MaterialTheme.colorScheme.surface,
                    sheetState = sheetState,
                    onDismissRequest = { isSheetOpen = false },
                ) {
                    coroutineScope.launch(Dispatchers.IO) {
                        async { genreList = getGenre() }.await()
                    }
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
                                            randMovieID = displayRandomMovie(randomizeGenre)
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



            // Below code for trending and theater carousels on search screen
            Column (
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = "Trending",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp).padding(bottom = 8.dp), // Add some top padding for spacing
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                    //.padding(padding)
                ) {
                    HorizontalMultiBrowseCarousel(
                        state = rememberCarouselState { trendingMovies.size },
                        // mess with this some more
                        modifier = Modifier.fillMaxWidth().height(221.dp),
                        preferredItemWidth = 160.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                            movie ->
                        Card {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w500${trendingMovies[movie].posterPath}",
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        navigateToMovieDetails(navController, trendingMovies[movie].id)
                                    }
                                    .width(135.dp)
                                    .aspectRatio(0.6667f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }


                Text(
                    text = "In Theaters",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp).padding(bottom = 8.dp), // Add some top/bottom padding for spacing
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                    //.padding(padding)
                ) {
                    HorizontalMultiBrowseCarousel(
                        state = rememberCarouselState { nowPlayingMovies.size },
                        modifier = Modifier.width(412.dp).height(221.dp),
                        preferredItemWidth = 160.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                            movie ->
                        Card {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w500${nowPlayingMovies[movie].posterPath}",
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        navigateToMovieDetails(
                                            navController,
                                            nowPlayingMovies[movie].id
                                        )
                                    }
                                    .width(135.dp)
                                    .aspectRatio(0.6667f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

        }
    }
}

// Shows the search results as a vertical grid after entering query
@Composable
fun SearchRows(movieList: List<MovieSearchResult>, navController: NavHostController) {
    if (movieList.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxHeight(.90f),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, start = 8.dp, end = 8.dp) // Add bottom padding
            ) {
            items(movieList) { movie ->
                Column(
                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Center the text
                ) {
                    Card {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    navigateToMovieDetails(navController, movie.id)
                                }
                                .fillMaxWidth()
                                .aspectRatio(0.6667f),
                            contentScale = ContentScale.Crop
                        )
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
    }
}

// function that handles navController and passes movieId to detail screen
fun navigateToMovieDetails(navController: NavHostController, movieId: Int) {
    navController.navigate(DetailDestination.createRoute(movieId))
}