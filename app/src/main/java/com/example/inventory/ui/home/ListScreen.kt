package com.example.inventory.ui.home

import android.annotation.SuppressLint
import android.view.View.OnHoverListener
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryApplication
import com.example.inventory.data.AppDatabase
import com.example.inventory.data.OfflineUserListRepository
import com.example.inventory.data.UserListRepository
import com.example.inventory.data.userlist.UserList

object ListDestination : NavigationDestination {
    override val route = "list"
    override val titleRes = R.string.list_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun ListScreen(navController: NavHostController){
    //val navController = rememberNavController()

    // for list selection sheet
    val userListRepository = InventoryApplication().container.userListRepository // use app container to get repository
    val listMoviesRepository = InventoryApplication().container.listMoviesRepository
    val movieRepository = InventoryApplication().container.movieRepository
    val viewModel: ListScreenViewModel = viewModel(factory = ListScreenViewModelFactory(userListRepository,
        listMoviesRepository,
        movieRepository)
    )
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showModal by remember { mutableStateOf(false) }

    // collect data from ListScreenViewModel
    val allLists by viewModel.allLists.collectAsState()
    val selectedList by viewModel.selectedList.collectAsState()
    val listMovies by viewModel.allMovies.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Lists"
                    )
                },

                actions = {
                    IconButton(onClick = { navController.navigate(SearchDestination.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(onClick = {/* TODO: Some shit idk yet */}) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More Stuff"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        showModal = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Create, // You can change the icon
                        contentDescription = "Edit"
                    )
                },
                text = { Text(selectedList?.listName ?: "All") },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.offset(y = -100.dp)
            )
        }
    ) {
        Column(modifier = Modifier.offset(y = 110.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {/*TODO: Sorting*/},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(0.dp),

                ) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Sorting")
                    Text("Sort")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {/*TODO: View*/},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "View")
                }
            }
            // TODO: put stuff for displaying the movies here
        }
    }
    // bottom sheet displays after clicking FAB
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState,
        ) {
            ListSelectBottomSheet(allLists, viewModel) { showModal = false }
        }
    }
}

@Composable
fun ListSelectBottomSheet(allLists: List<UserList>, viewModel: ListScreenViewModel, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.padding(1.dp)
    ) { // first item in list is always All
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectList(null)
                    onDismiss()
                }
        ) {
            Text(
                text = "All",
                modifier = Modifier.padding(10.dp)
            )
        }
        allLists.forEach() { singleList ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.selectList(singleList)
                        onDismiss()
                    }
            ) {
                Text(
                    text = singleList.listName,
                    modifier = Modifier.padding(10.dp)
                )
            }

        }

    }
}