package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.data.api.getMovieQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material3.Card
import com.example.inventory.data.Movie
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay

object SearchDestination : NavigationDestination {
    override val route = "search"
    override val titleRes = R.string.search_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
@Preview
fun SearchScreen() {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var tempMovieList by remember { mutableStateOf(mutableListOf<Movie>()) } // mutableStateOf to trigger recomposition

    val coroutineScope = rememberCoroutineScope()

    // Animate padding based on the active state
    val searchBarPadding by animateDpAsState(
        targetValue = if (active) 0.dp else 16.dp,
        label = "Search bar padding"
    )

    // Keyboard controller to manage keyboard visibility
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = searchBarPadding),
            query = text,
            onQueryChange = {
                text = it
            },
            onSearch = {
                // Hide the keyboard when search is pressed
                keyboardController?.hide()

                // Keep the search bar active and trigger search
                coroutineScope.launch(Dispatchers.IO) {
                    delay(200)
                    val result = async {
                        tempMovieList = getMovieQuery(text)
                    }.await()

                    // No need to change 'active' here, so the search bar stays open
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
                        modifier = Modifier.clickable { active = false },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Icon"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
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
            // Display movie search results if any
            if (text.isNotEmpty()){
                SearchRows(tempMovieList)
            }else{
                tempMovieList.clear()
            }
        }
    }
}

@Composable
fun SearchRows(movieList: List<Movie>) {
    if (movieList.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxHeight(.90f),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),

        ) {
            items(movieList) { movie ->
                Card( modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp)){
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = null, modifier = Modifier.clickable { println("clicked") }
                    )
                }
            }
        }
    }
}