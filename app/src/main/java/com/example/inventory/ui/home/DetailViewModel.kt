package com.example.inventory.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ListMoviesRepository
import com.example.inventory.data.MovieRepository
import com.example.inventory.data.UserListRepository
import com.example.inventory.data.api.MovieCast
import com.example.inventory.data.api.MovieSearchResult
import com.example.inventory.data.api.getMovieCast
import com.example.inventory.data.api.getRecommendedMovies
import com.example.inventory.data.listmovies.ListMovies
import com.example.inventory.data.movie.Movie
import com.example.inventory.data.userlist.UserList
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
) : ViewModel() {


    var recommendedMovies by mutableStateOf(listOf<MovieSearchResult>())
        private set

    var movieCast by mutableStateOf(listOf<MovieCast>())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recommendedMovies = getRecommendedMovies(currMovieID)
            movieCast = getMovieCast(currMovieID)
        }
    }

    // used for displaying in modal bottom sheet, no need to pull from db
    val defaultLists: List<UserList> =  listOf(UserList("Planning"), UserList("Watching"), UserList("Completed"))

    // gets movie counts for default lists, only way i could think of doing this.
    val completedCount: StateFlow<Int> = userListRepository.getMovieCountStream("Completed").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    val watchingCount: StateFlow<Int> = userListRepository.getMovieCountStream("Watching").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    val planningCount: StateFlow<Int> = userListRepository.getMovieCountStream("Planning").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // StateFlow for displaying all lists in the bottom screen sheet
    val allLists: StateFlow<List<UserList>> = userListRepository.getAllListsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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
                listMoviesRepository.insertListMovieRelation(ListMovies(listName, movie.movieID)) //add to ListMovies relation table
                userListRepository.incMovieCount(listName) // increment the list's movie count
            }
        }
    }

    fun moveMovieToList(oldListName: String, newListName: String) {
        viewModelScope.launch {
            // remove the other relation first and then insert new one
            listMoviesRepository.deleteListMovieRelation(ListMovies(oldListName, currMovieID))
            userListRepository.decMovieCount(oldListName) // decrement the old list's movie count
            listMoviesRepository.insertListMovieRelation(ListMovies(newListName, currMovieID))
            userListRepository.incMovieCount(newListName) // increment the new list's movie count
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