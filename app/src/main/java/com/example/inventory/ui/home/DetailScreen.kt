package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination


object DetailDestination : NavigationDestination {
    override val route = "details"
    override val titleRes = R.string.movie_details // Add a string resource for the title
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieDetailsScreen(/*movieId: String?,*/ navController: NavHostController) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var isTopAppBarScrolled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    if (isTopAppBarScrolled) {
                        Text(
                            text = "Yeah....I'm man", // Replace with actual movie title when available
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = padding.calculateBottomPadding())
                .padding(bottom = 88.dp)
        ) {
            // Cover Art (replace with AsyncImage when you have the movie data)
            AsyncImage(
                model = "https://preview.redd.it/thoughts-on-my-man-edit-v0-km5g7hq4fnbc1.jpeg?width=750&format=pjpg&auto=webp&s=d05a0ad74a914f35affb37c9e29b0e95850d7ac4",
                contentDescription = null,
                modifier = Modifier
                    .clickable {navController.navigate(DetailDestination.route)}
                    .fillMaxWidth()
                    .aspectRatio(0.6667f),
                contentScale = ContentScale.Crop
            )

            // Title (replace with actual title from movie data)
            Text(
                text = "Movie Title", // Replace with actual movie title when available
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // You can add more details like runtime, release date, rating, synopsis, etc. here
            // ...
        }
    }
}