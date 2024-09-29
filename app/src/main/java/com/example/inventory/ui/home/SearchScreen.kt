package com.example.inventory.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Message
import android.provider.ContactsContract.Contacts.Photo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.BuildCompat
import androidx.room.util.query
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.BuildConfig
import com.example.inventory.data.api.getMovieQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import com.example.inventory.data.Movie
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

object SearchDestination : NavigationDestination {
    override val route = "search"
    override val titleRes = R.string.search_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview
fun SearchScreen() {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var tempMovie: MutableList<Movie> = mutableListOf()

//    val api_key = BuildConfig.API_KEY //might not need this
    val coroutineScope = rememberCoroutineScope()
    Scaffold {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = {
                text = it
            },
            onSearch = {
                active = false
                coroutineScope.launch(Dispatchers.IO){
//                    delay(800)
                    val result = async {
                        tempMovie = getMovieQuery(text)
                    }.await()

                    launch(Dispatchers.IO){
                    }
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
                if(active) {
                    Icon(
                        modifier = Modifier.clickable {
                            active = false
                        },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Icon"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                }
            },
            trailingIcon = {
                if(active) {
                    Icon(
                        modifier = Modifier.clickable {
                            text = ""
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon"
                    )
                }
            }
        ){
            SearchRows(tempMovie)
        }
    }
}

@Composable
fun SearchRows(movieList: MutableList<Movie>){
    println(movieList + "dfd")
    LazyVerticalGrid (
        columns = GridCells.Fixed(2),
        horizontalArrangement =
            Arrangement.spacedBy(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(24.dp)
    ){
        items(movieList) { movie ->
            Card() {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = ""
                )
            }
        }
    }
}