package com.example.inventory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class SearchViewModel(itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds search ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [SearchUiState]
     */
    val searchUiState: StateFlow<SearchUiState> =
        itemsRepository.getAllItemsStream().map { SearchUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SearchUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for SearchScreen
 */
data class SearchUiState(val itemList: List<Item> = listOf())
