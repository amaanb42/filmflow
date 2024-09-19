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
class CompletedViewModel(itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds completed ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [CompletedUiState]
     */
    val completedUiState: StateFlow<CompletedUiState> =
        itemsRepository.getAllItemsStream().map { CompletedUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CompletedUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for CompletedScreen
 */
data class CompletedUiState(val itemList: List<Item> = listOf())