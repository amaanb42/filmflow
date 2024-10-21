package com.example.inventory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ListMoviesRepository
import com.example.inventory.data.MovieRepository
import com.example.inventory.data.OfflineListMoviesRepository
import com.example.inventory.data.OfflineUserListRepository
import com.example.inventory.data.UserListRepository
import com.example.inventory.data.movie.Movie
import com.example.inventory.data.userlist.UserList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn

class ListScreenViewModel(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {

    // StateFlow for displaying all lists in the bottom screen sheet
    val allLists: StateFlow<List<UserList>> = userListRepository.getAllListsStream().stateIn(
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
// pass the repository to ListScreenViewModel
class ListScreenViewModelFactory(
    private val userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListScreenViewModel::class.java)) {
            return ListScreenViewModel(userListRepository, listMoviesRepository, movieRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ListScreenViewModel")
    }
}