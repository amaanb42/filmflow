package com.example.inventory.ui.home

import android.icu.text.DecimalFormat
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.inventory.R
import com.example.inventory.data.api.MovieDetails


// For formatting the TMDB community rating into a nice percentage
@Composable
fun RatingText(movieDetails: MovieDetails?) {
    val rating = movieDetails?.audienceRating

    val formattedRating = if (rating != null) {
        val percentage = rating * 10 // Multiply by 10 to shift decimal place
        val formattedPercentage = DecimalFormat("0").format(percentage) //Format to a whole number. You can also use "0.0" if you want 1 decimal place
        "$formattedPercentage%"
    } else {
        "N/A" // Or handle the null case as needed, e.g., "N/A"
    }

    Text(
        text = formattedRating,
        style = MaterialTheme.typography.bodyMedium
    )
}

// For Status Buttons on detail screen
@Composable
fun SegmentedButtons(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier,
) {
    val listsForMovie by viewModel.listsForMovie.collectAsState()
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val items = listOf(
        R.drawable.planning_icon,
        R.drawable.watching_icon,
        R.drawable.completed_icon
    )
    val listNames = listOf("Planning", "Watching", "Completed")

    // Determine the initial selected index based on the movie's current list
    val status: String =
        if ("Completed" in listsForMovie)
            "Completed"
        else if ("Watching" in listsForMovie)
            "Watching"
        else
            "Planning"

    // Set the initial selected index based on the status
    LaunchedEffect(key1 = status) {
        selectedItemIndex = listNames.indexOf(status)
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            key(selectedItemIndex) { // Add this key
                ListIconButton(
                    icon = item,
                    isSelected = selectedItemIndex == index,
                    onClick = {
                        val currentList =
                            listNames.find { it in listsForMovie } // Find the current list
                        val newList = listNames[index]
                        if (currentList != null && currentList != newList) {
                            viewModel.moveMovieToList(
                                currentList,
                                newList
                            ) // Move only if necessary
                        }
                        selectedItemIndex = index
                    },
                    label = listNames[index] // displays list name for icon
                )
            }
            if (index < items.size - 1) {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}

@Composable
fun ListIconButton(
    icon: Int, // Changed to Int for drawable resource ID
    isSelected: Boolean,
    onClick: () -> Unit,
    label: String, // Label for buttons
) {
    val animateSurfaceColor = animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 400)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() } // Handle click outside the Surface
    ) {
        Surface(
            modifier = Modifier
                .selectable(
                    selected = isSelected,
                    onClick = onClick,
                    role = Role.Button
                ),
            shape = MaterialTheme.shapes.small,
            color = animateSurfaceColor.value
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null
                )
            }
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    }
}

@Composable
fun StatusButtons(
    viewModel: DetailViewModel // Add the ViewModel
) {
    Column {
        SegmentedButtons(viewModel = viewModel)
    }
}


// for detail screen runtime
fun formatRuntime(minutes: Int?): String {
    val runtime = minutes ?: 0 // Use 0 as the default value if minutes is null
    val hours = runtime / 60
    val remainingMinutes = runtime % 60
    return when {
        hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
        hours > 0 -> "${hours}h"
        else -> "${remainingMinutes}m"
    }
}