package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.data.userlist.UserList
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.dark_pine
import kotlinx.coroutines.launch

object ListDestination : NavigationDestination {
    override val route = "list"
    override val titleRes = R.string.list_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(navController: NavHostController){

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
    //val currList = selectedList?.listName // used for highlighting selection in bottom sheet

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // "Movie List" by default but changes depending on list selected
                    Text(text = selectedList?.listName ?: "Movie List")
                },

                actions = {
                    IconButton(onClick = { /*Handle search*/ }) {
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
                    // Choose icon based on selectedList
                    // As the code currently is, if a user makes a custom list, the FAB icon
                    // will be the same as the All icon instead of the custom icon in the bottom sheet
                    val icon = when (selectedList?.listName) {
                        "Completed" -> painterResource(id = R.drawable.completed_icon)
                        "Planning" -> painterResource(id = R.drawable.planning_icon)
                        "Watching" -> painterResource(id = R.drawable.watching_icon)
                        else -> painterResource(id = R.drawable.all_icon) // Default icon
                    }

                    Icon(
                        painter = icon,
                        contentDescription = "Edit"
                    )
                },
                text = { Text(selectedList?.listName ?: "All") },
                containerColor = dark_pine,
                contentColor = Color.White,
                modifier = Modifier.offset(y = (-100).dp)
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
            ListSelectBottomSheet(allLists, viewModel, selectedList) { showModal = false }
        }
    }
}

@Composable
fun ListSelectBottomSheet(allLists: List<UserList>, viewModel: ListScreenViewModel, currList: UserList?, onDismiss: () -> Unit) {
    // these are used for the rename list dialog
    var showRenameDialog by remember { mutableStateOf(false) }
    var oldListName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.5f).dp) // Set the height to half the screen
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp, end = 2.dp, bottom = 100.dp)
        ) {
            // first item in list is always All, but in settings screen add option to change default list displayed
            item {
                Row(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectList(null)
                            onDismiss()
                        }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.all_icon), // Or any other suitable icon
                        contentDescription = "All",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "All",
                        fontWeight = if (currList == null) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            // separate default lists and user-created lists, so that all user-created lists appear after defaults
            // display default lists first
            items(viewModel.defaultLists) { defaultList ->
                Row(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectList(defaultList)
                            onDismiss()
                        }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Choose icon based on singleList.listName
                    val icon = when (defaultList.listName) {
                        "Completed" -> R.drawable.completed_icon
                        "Planning" -> R.drawable.planning_icon
                        "Watching" -> R.drawable.watching_icon
                        else -> R.drawable.custom_list // custom icon when user makes list
                    }

                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = defaultList.listName,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = defaultList.listName,
                        fontWeight = if (defaultList.listName == currList?.listName) FontWeight.ExtraBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp))
            }
            // now display lists stored in the DB
            items(allLists) { singleList ->
                if (singleList.listName !in listOf("Completed", "Planning", "Watching")) {
                    var expanded by remember { mutableStateOf(false) } // State for dropdown menu
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectList(singleList)
                                onDismiss()
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.custom_list), // custom icon when user makes list
                            contentDescription = singleList.listName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = singleList.listName,
                            fontWeight = if (singleList.listName == currList?.listName) FontWeight.ExtraBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Box {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                modifier = Modifier
                                    .clickable {
                                        expanded = true
                                    }
                            )
                            // Dropdown menu for MoreVert icon
                            DropdownMenu(
                                shape = RoundedCornerShape(18.dp),
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                }, // Close the menu when clicked outside
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "Rename") },
                                    onClick = {
                                        showRenameDialog =
                                            true // show the rename list dialog
                                        oldListName = singleList.listName
                                        expanded = false // Close the menu
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "Delete", color = Color.Red) },
                                    onClick = {
                                        viewModel.deleteList(singleList.listName) // delete the list
                                        viewModel.selectList(null) // reset back to default "All"
                                        expanded = false // Close the menu
                                    }
                                )

                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
                .padding(bottom = 30.dp)
        ) {
            // button for creating a new list
            AddNewListButtonWithDialog(viewModel)
        }
    }
    // dialog for renaming a list
    if (showRenameDialog) {
        var newListName by remember { mutableStateOf(oldListName) }

        AlertDialog(
            onDismissRequest = { showRenameDialog = false},
            title = { Text(text = "Rename") },
            text = {
                Column {
                    Text(text = "Give your list a new name:")
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        shape = RoundedCornerShape(18.dp),
                        value = newListName,
                        onValueChange = { newListName = it },
                        label = { Text(
                            "New Name",
                            color = LocalContentColor.current.copy(alpha = 0.5f) // makes text more transparent
                        ) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Text(
                    "Rename",
                    modifier = Modifier
                        .clickable {
                            // TODO: fix crash when trying to rename current list to another list that already exists
                            viewModel.renameList(oldListName, newListName)
                            // need to select list again
                            if (currList?.listName == oldListName) {
                                viewModel.selectList(UserList(newListName))
                            }
                            showRenameDialog = false
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            },
            dismissButton = {
                Text(
                    "Cancel",
                    modifier = Modifier
                        .clickable {
                            showRenameDialog = false
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            }
        )
    }
}

@Composable
fun AddNewListButtonWithDialog(viewModel: ListScreenViewModel) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var listName by remember { mutableStateOf("") }

    // the button
    Row(
        modifier = Modifier
            .padding(start = 2.dp, end = 2.dp)
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f)) //pushes button to center
        SmallFloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = dark_pine,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create new list",
            )
        }
        Spacer(modifier = Modifier.weight(1f)) //fills remaining space
    }
    // the dialog for creating a new custom list
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(text = "Create a List") },
            text = {
                Column {
                    Text(text = "Enter a name for your new list:")
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        shape = RoundedCornerShape(18.dp),
                        value = listName,
                        onValueChange = { listName = it },
                        label = {
                            Text(
                                "Name",
                                color = LocalContentColor.current.copy(alpha = 0.5f) // makes text more transparent
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Text(
                    "Create",
                    modifier = Modifier
                        .clickable {
                            viewModel.addNewList(listName) // insert list into db
                            showCreateDialog = false
                            listName = ""
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            },
            dismissButton = {
                Text(
                    "Cancel",
                    modifier = Modifier
                        .clickable {
                            showCreateDialog = false
                            listName = ""
                        }
                        .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                )
            }
        )
    }
}