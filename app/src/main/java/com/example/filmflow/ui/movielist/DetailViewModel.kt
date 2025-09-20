package com.example.filmflow.ui.movielist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.filmflow.data.ListMoviesRepository
import com.example.filmflow.data.MovieRepository
import com.example.filmflow.data.UserListRepository
import com.example.filmflow.data.api.MediaCast
import com.example.filmflow.data.api.MovieSearchResult
import com.example.filmflow.data.api.getCollectionIdForMovie
import com.example.filmflow.data.api.getCollectionNameForMovie
import com.example.filmflow.data.api.getMovieCast
import com.example.filmflow.data.api.getMovieCollection
import com.example.filmflow.data.api.getRecommendedMovies
import com.example.filmflow.data.listmovies.ListMovies
import com.example.filmflow.data.movie.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository,
    private val currMovieID: Int
    //private val collectionID: Int
) : ViewModel() {

    var mediaCast by mutableStateOf(listOf<MediaCast>())
        private set

    var recommendedMovies by mutableStateOf(listOf<MovieSearchResult>())
        private set

    var movieCollection by mutableStateOf(listOf<MovieSearchResult>())
        private set

    var movieCollectionName by mutableStateOf<String?>(null) // Store the collection name
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mediaCast = getMovieCast(currMovieID)
            movieCollection = getCollectionIdForMovie(currMovieID)?.let { getMovieCollection(it) } ?: listOf() // assigns empty list if movie does not have a collection
            movieCollectionName = getCollectionNameForMovie(currMovieID)
            recommendedMovies = getRecommendedMovies(currMovieID)
        }
    }

    val listsForMovie: StateFlow<List<String>> = listMoviesRepository.getListsForMovieStream(currMovieID).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // adding a movie to a list
    fun addMovieToList(listName: String, movie: Movie) {
        viewModelScope.launch {
            movieRepository.insertMovie(movie) // have to add to Movie table first
            if (listName !in listsForMovie.value) { // if movie isn't already in the selected list
                listMoviesRepository.insertListMovieRelation(ListMovies(listName = listName, movieID = movie.movieID)) //add to ListMovies relation table
                userListRepository.incMovieCount(listName) // increment the list's movie count
            }
        }
    }

    fun moveMovieToList(oldListName: String, newListName: String) {
        viewModelScope.launch {
            // remove the other relation first and then insert new one
            listMoviesRepository.deleteListMovieRelation(ListMovies(listName = oldListName, movieID = currMovieID))
            userListRepository.decMovieCount(oldListName) // decrement the old list's movie count
            listMoviesRepository.insertListMovieRelation(ListMovies(listName = newListName, movieID = currMovieID))
            userListRepository.incMovieCount(newListName) // increment the new list's movie count
        }
    }

    // update rating of locally stored movie
    fun changeMovieRating(movieID: Int, newRating: Float) {
        viewModelScope.launch {
            movieRepository.updateUserRating(movieID, newRating)
        }
    }

    // deletes a movie from the DB and its relations
    fun deleteMovie(movieID: Int) {
        viewModelScope.launch {
            for (listName in listsForMovie.value) { // decrement movie count for each list the movie was in
                userListRepository.decMovieCount(listName)
            }
            movieRepository.deleteMovieByID(movieID)
        }
    }
}
// pass the repository to DetailViewModel
@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository,
    private val currMovieID: Int
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(userListRepository, listMoviesRepository, movieRepository, currMovieID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ListScreenViewModel")
    }
}