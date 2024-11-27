package com.example.inventory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.data.ListMoviesRepository
import com.example.inventory.data.MovieRepository
import com.example.inventory.data.UserListRepository
import com.example.inventory.data.listmovies.ListMovies
import com.example.inventory.data.movie.Movie
import com.example.inventory.data.userlist.UserList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocalDetailViewModel(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository,
    private val currMovieID: Int
) : ViewModel() {

    // used for displaying in modal bottom sheet, no need to pull from db
    val defaultLists: List<UserList> =  listOf(UserList("Planning"), UserList("Watching"), UserList("Completed"))

    // StateFlow for displaying all lists in the bottom screen sheet
    val allLists: StateFlow<List<UserList>> = userListRepository.getAllListsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // used to determine the lists the current movie is in
    val listsForMovie: StateFlow<List<String>> = listMoviesRepository.getListsForMovieStream(currMovieID).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // update rating of locally stored movie
    fun changeMovieRating(movieID: Int, newRating: Float) {
        viewModelScope.launch {
            movieRepository.updateUserRating(movieID, newRating)
        }
    }

    // used on local detail screen to copy over movie to another user-created list
    fun addMovieToList(listName: String) {
        viewModelScope.launch {
            if (listName !in listsForMovie.value) { // have to add to Movie table first
                listMoviesRepository.insertListMovieRelation(ListMovies(listName, currMovieID)) //add to ListMovies relation table
                userListRepository.incMovieCount(listName) // increment the list's movie count
            }
        }
    }

    // use for changing status of movie between default lists
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
class LocalDetailViewModelFactory(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository,
    private val currMovieID: Int
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocalDetailViewModel::class.java)) {
            return LocalDetailViewModel(userListRepository, listMoviesRepository, movieRepository, currMovieID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class LocalDetailViewModel")
    }
}

