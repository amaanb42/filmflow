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
    private val movieRepository: MovieRepository
) : ViewModel() {

    // used for displaying in modal bottom sheet, no need to pull from db
    val defaultLists: List<UserList> = listOf(UserList("Completed"), UserList("Planning"), UserList("Watching"))

    // StateFlow for displaying all lists in the bottom screen sheet
    val allLists: StateFlow<List<UserList>> = userListRepository.getAllListsStream().stateIn(
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

    // TODO: put movie deletion/moving functions below here
    // used on local detail screen to copy over movie to another user-created list
    fun addMovieToList(listName: String, movie: Movie) {
        viewModelScope.launch {
            listMoviesRepository.insertListMovieRelation(ListMovies(listName, movie.movieID)) //add to ListMovies relation table
        }
    }
    // use for changing status of movie between default lists
    fun moveMovieToList(oldListName: String, newListName: String, movie: Movie) {
        viewModelScope.launch {
            // remove the other relation first and then insert new one
            listMoviesRepository.deleteListMovieRelation(ListMovies(oldListName, movie.movieID))
            listMoviesRepository.insertListMovieRelation(ListMovies(newListName, movie.movieID))
        }
    }
}
// pass the repository to DetailViewModel
class LocalDetailViewModelFactory(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocalDetailViewModel::class.java)) {
            return LocalDetailViewModel(userListRepository, listMoviesRepository, movieRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class LocalDetailViewModel")
    }
}

