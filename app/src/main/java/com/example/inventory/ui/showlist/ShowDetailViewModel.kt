package com.example.inventory.ui.showlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ListShowsRepository
import com.example.inventory.data.ShowRepository
import com.example.inventory.data.UserListRepository
import com.example.inventory.data.api.MediaCast
import com.example.inventory.data.api.ShowSearchResult
import com.example.inventory.data.api.getShowCast
import com.example.inventory.data.api.getRecommendedShows
import com.example.inventory.data.listshows.ListShows
import com.example.inventory.data.show.Show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShowDetailViewModel(
    private val userListRepository: UserListRepository,
    private val listShowsRepository: ListShowsRepository,
    private val showRepository: ShowRepository,
    private val currShowID: Int
    //private val collectionID: Int
) : ViewModel() {

    var showCast by mutableStateOf(listOf<MediaCast>())
        private set

    var recommendedShows by mutableStateOf(listOf<ShowSearchResult>())
        private set

    var showCollection by mutableStateOf(listOf<ShowSearchResult>())
        private set

    var showCollectionName by mutableStateOf<String?>(null) // Store the collection name
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            showCast = getShowCast(currShowID)
            recommendedShows = getRecommendedShows(currShowID)
        }
    }

    val listsForShow: StateFlow<List<String>> = listShowsRepository.getListsForShowStream(currShowID).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // adding a show to a list
    fun addShowToList(listName: String, show: Show) {
        viewModelScope.launch {
            showRepository.insertShow(show) // have to add to Show table first
            if (listName !in listsForShow.value) { // if show isn't already in the selected list
                listShowsRepository.insertListShowRelation(ListShows(listName = listName, showID = show.showID)) //add to ListShows relation table
                userListRepository.incShowCount(listName) // increment the list's show count
            }
        }
    }

    fun moveShowToList(oldListName: String, newListName: String) {
        viewModelScope.launch {
            // remove the other relation first and then insert new one
            listShowsRepository.deleteListShowRelation(ListShows(listName = oldListName, showID = currShowID))
            userListRepository.decShowCount(oldListName) // decrement the old list's show count
            listShowsRepository.insertListShowRelation(ListShows(listName = newListName, showID = currShowID))
            userListRepository.incShowCount(newListName) // increment the new list's show count
        }
    }

    // update rating of locally stored show
    fun changeShowRating(showID: Int, newRating: Float) {
        viewModelScope.launch {
            showRepository.updateUserRating(showID, newRating)
        }
    }

    // deletes a show from the DB and its relations
    fun deleteShow(showID: Int) {
        viewModelScope.launch {
            for (listName in listsForShow.value) { // decrement show count for each list the show was in
                userListRepository.decShowCount(listName)
            }
            showRepository.deleteShowByID(showID)
        }
    }
}
// pass the repository to DetailViewModel
@Suppress("UNCHECKED_CAST")
class ShowDetailViewModelFactory(
    private val userListRepository: UserListRepository,
    private val listShowsRepository: ListShowsRepository,
    private val showRepository: ShowRepository,
    private val currShowID: Int
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowDetailViewModel::class.java)) {
            return ShowDetailViewModel(userListRepository, listShowsRepository, showRepository, currShowID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ListScreenViewModel")
    }
}