/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopSearchBar
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.item.formattedRating
import com.example.inventory.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clip

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToEditItem: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopSearchBar(
                //title = stringResource(CompletedDestination.titleRes),
                canNavigateBack = false,
                searchQuery = searchQuery, // Pass searchQuery to the TopAppBar
                onSearchQueryChanged = { query -> searchQuery = query },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedFloatingActionButton(
                listState = listState,
                navigateToItemEntry = navigateToItemEntry
            )
        }
    ) { innerPadding ->
        HomeBody(
            itemList = homeUiState.itemList,
            searchQuery = searchQuery,
            onItemClick = navigateToEditItem,
            listState = listState,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun HomeBody(
    itemList: List<Item>,
    searchQuery: String,
    onItemClick: (Int) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    // Filter items based on search query and not watched status
    val filteredItems = itemList.filter {
        !it.isWatched && it.title.contains(searchQuery, ignoreCase = true)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center // This centers the content vertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally // This aligns the text to the center horizontally
                ) {
                    Text(text = stringResource(R.string.no_item_description), style = MaterialTheme.typography.titleLarge)
                }
            }
        } else {
            InventoryList(
                itemList = filteredItems,
                onItemClick = { onItemClick(it.id) },
                listState = listState,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                    .padding(bottom = 80.dp)
            )
        }
    }
}

@Composable
private fun InventoryList(
    itemList: List<Item>, onItemClick: (Item) -> Unit, listState: LazyListState, modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(
            top = dimensionResource(id = R.dimen.padding_small), // Add top padding
            bottom = dimensionResource(id = R.dimen.padding_small) // Add bottom padding
        )
    ) {
        items(items = itemList, key = { it.id }) { item ->
            if (!item.isWatched) {
                InventoryItem(
                    item = item,
                    onItemClick = { onItemClick(item) },
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }
}


@Composable
fun InventoryItem(
    item: Item,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val ripple = rememberRipple(bounded = true)

    Card(
        shape = shape,
        //colors = CardDefaults.cardColors(theme_cards),
        modifier = modifier
            .clip(shape) // Clip to the shape
            .clickable(
                interactionSource = interactionSource,
                indication = ripple,
                onClick = { onItemClick(item) }
            )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = item.formattedRating(),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
fun LazyListState.isScrollingUp(): State<Boolean> {
    return produceState(initialValue = true) { // Initial value set to true so the FAB shows by default
        var lastScrollOffset = this@isScrollingUp.firstVisibleItemScrollOffset
        var lastVisibleItemIndex = this@isScrollingUp.firstVisibleItemIndex

        snapshotFlow { firstVisibleItemIndex to firstVisibleItemScrollOffset }
            .collect { (currentIndex, currentScroll) ->
                val isScrollingUpNow = currentIndex < lastVisibleItemIndex ||
                        (currentIndex == lastVisibleItemIndex && currentScroll < lastScrollOffset)

                if (currentIndex != lastVisibleItemIndex || currentScroll != lastScrollOffset) {
                    value = isScrollingUpNow
                    lastScrollOffset = currentScroll
                    lastVisibleItemIndex = currentIndex
                }
            }
    }
}

@Composable
fun AnimatedFloatingActionButton(
    listState: LazyListState,
    navigateToItemEntry: () -> Unit
) {
    val isScrollingUp = listState.isScrollingUp().value

    AnimatedVisibility(
        visible = isScrollingUp,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        FloatingActionButton(
            onClick = navigateToItemEntry,
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_large))
                .padding(bottom = 60.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.item_entry_title)
            )
        }
    }
}

