package com.example.filmflow.ui.discover

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmflow.data.api.MovieSearchResult
import com.example.filmflow.data.api.getNowPlayingMovies
import com.example.filmflow.data.api.getTrendingMovies
import com.example.filmflow.data.api.getUpcomingMovies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchScreenViewModel : ViewModel() {

    var trendingMovies by mutableStateOf(listOf<MovieSearchResult>())
        private set

    var nowPlayingMovies by mutableStateOf(listOf<MovieSearchResult>())
        private set

    var upcomingMovies by mutableStateOf(listOf<MovieSearchResult>())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            trendingMovies = getTrendingMovies()
            nowPlayingMovies = getNowPlayingMovies()
            upcomingMovies = getUpcomingMovies()
        }
    }
}
