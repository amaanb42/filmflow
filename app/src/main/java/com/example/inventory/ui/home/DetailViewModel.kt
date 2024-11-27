package com.example.inventory.ui.home

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

class DetailViewModel(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository,
    private val currMovieID: Int
) : ViewModel() {

    // adding a movie to a list
    fun addMovieToList(listName: String, movie: Movie) {
        viewModelScope.launch {
            movieRepository.insertMovie(movie) //have to add to Movie table first
            listMoviesRepository.insertListMovieRelation(ListMovies(listName, movie.movieID)) //add to ListMovies relation table
        }
    }

    fun moveMovieToList(oldListName: String, newListName: String, movie: Movie) {
        viewModelScope.launch {
            // remove the other relation first and then insert new one
            listMoviesRepository.deleteListMovieRelation(ListMovies(oldListName, movie.movieID))
            listMoviesRepository.insertListMovieRelation(ListMovies(newListName, movie.movieID))
        }
    }

    // used for displaying in modal bottom sheet, no need to pull from db
    val defaultLists: List<UserList> =  listOf(UserList("Planning"), UserList("Watching"), UserList("Completed"))

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

    // StateFlow to hold the currently selected list
    private val _selectedList = MutableStateFlow<UserList?>(null)
    val selectedList: StateFlow<UserList?> = _selectedList

    // Function to update the selected list
    fun selectList(singleList: UserList?) {
        _selectedList.value = singleList
    }

    // StateFlow for displaying all movies on List Screen, default on app start
    var allMovies: StateFlow<List<Movie>> = movieRepository.getAllMoviesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Function to filter movies based on list selection
    fun updateListMovies(singleList: UserList?) {
        allMovies = if (singleList == null) { // "All" is selected
            movieRepository.getAllMoviesStream().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
        } else { // any other list is selected
            listMoviesRepository.getMoviesForListStream(singleList.listName).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
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