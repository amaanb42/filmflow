package com.example.filmflow.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.filmflow.data.ListMoviesRepository
import com.example.filmflow.data.MovieRepository
import com.example.filmflow.data.UserListRepository
import com.example.filmflow.data.movie.Movie
import com.example.filmflow.data.userlist.UserList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ListScreenViewModel(
    userListRepository: UserListRepository,
    private val listMoviesRepository: ListMoviesRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {
    // for remembering list view
    private val _showGridView = MutableStateFlow(true)
    val showGridView: StateFlow<Boolean> = _showGridView
    fun changeListView() {
        _showGridView.value = !_showGridView.value
    }

    // used for displaying in modal bottom sheet, no need to pull from db
    val defaultLists: List<UserList> = listOf(UserList("Planning"), UserList("Watching"), UserList("Completed"))

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

    // StateFlow to hold the currently selected list
    private val _selectedList = MutableStateFlow("")
    val selectedList: StateFlow<String> = _selectedList

    // Function to update the selected list
    fun selectList(singleList: String) {
        _selectedList.value = singleList
    }

    // StateFlow for displaying all movies on List Screen, default on app start
    var allMovies: StateFlow<List<Movie>> = movieRepository.getAllMoviesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Function to filter movies based on list selection
    fun updateListMovies(singleList: String) {
        allMovies = if (singleList == "") { // "All" is selected
            movieRepository.getAllMoviesStream().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
        } else { // any other list is selected
            listMoviesRepository.getMoviesForListStream(singleList).stateIn(
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
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListScreenViewModel::class.java)) {
            return ListScreenViewModel(userListRepository, listMoviesRepository, movieRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ListScreenViewModel")
    }
}