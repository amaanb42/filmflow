package com.example.inventory.ui.home

import android.icu.text.DecimalFormat
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtons(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val listsForMovie by viewModel.listsForMovie.collectAsState()
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val items = listOf(
        R.drawable.planning_icon,
        R.drawable.watching_icon,
        R.drawable.completed_icon,
//        R.drawable.paused,
//        R.drawable.dropped
    )
    val listNames = listOf("Planning", "Watching", "Completed")

    val tooltipPosition = TooltipDefaults.rememberPlainTooltipPositionProvider()

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
                TooltipBox(positionProvider = tooltipPosition,
                    tooltip = {
                        // Plain tooltip to show user the list they clicked on
                        PlainTooltip {
                            Text(listNames[index])
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    ListIconButton(
                        icon = item,
                        isSelected = selectedItemIndex == index,
                        onClick = {
                            val currentList =
                                listNames.find { it in listsForMovie } // Find the current list
                            val newList = listNames[index]
                            if (currentList != null && currentList != newList) {
                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                                viewModel.moveMovieToList(
                                    currentList,
                                    newList
                                ) // Move only if necessary
                            }
                            selectedItemIndex = index
                        }
                    )
                }
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
) {
    val animateSurfaceColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 400)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        //modifier = Modifier.clickable { onClick() } // Handle click outside the Surface
    ) {
        Surface(
            modifier = Modifier
                .selectable(
                    selected = isSelected,
                    onClick = onClick,
                    role = Role.Button
                ),
            shape = MaterialTheme.shapes.small,
            color = animateSurfaceColor
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
//        Text(
//            text = label,
//            modifier = Modifier.padding(top = 4.dp),
//            fontSize = MaterialTheme.typography.bodyMedium.fontSize
//        )
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

@Composable
fun RatingCircle(
    userRating: Float, // Now takes userRating directly
    fontSize: TextUnit,
    radius: Dp,
    // color: Color = MaterialTheme.colorScheme.outline,
    strokeWidth: Dp,
    animDuration: Int,
    animDelay: Int = 100
) {
    var animationPlayed by remember { mutableStateOf(false) }

    // Calculate percentage based on userRating (0 to 10 scale)
    val percentage = userRating / 10f

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        label = "Rating Animation",
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )
    LaunchedEffect(key1 = true) { animationPlayed = true }

    // Determine the color based on userRating
    // Use animateColorAsState for smooth color transitions
    val color = MaterialTheme.colorScheme.primary
//    val color = animateColorAsState(
//        targetValue = when (userRating) {
//            in 0.0f..2.9f -> material_red        // 0 - 2.9: Red
//            in 3.0f..4.9f -> material_orange     // 3 - 4.9: Orange
//            in 5.0f..6.9f -> material_yellow     // 5 - 6.9: Yellow
//            in 7.0f..8.9f -> material_green      // 7 - 8.9: Green
//            in 9.0f..10.0f -> MaterialTheme.colorScheme.primary // 9 - 10: Blue
//            else -> MaterialTheme.colorScheme.outline // Default color
//        },
//        label = "Color Animation" // Add a label for debugging
//    ).value

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Canvas(
            modifier = Modifier.size(radius * 2f)
            //.clip(CircleShape)
        ) {
            // Draw the white circle first
            drawCircle(
                color = Color.White,
                radius = radius.toPx(), // Adjust radius for the stroke width
                style = Stroke(1.dp.toPx()) // Thin stroke width
            )

            // Draw arc on top
            drawArc(
                color = color,
                -90f,
                360 * curPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = String.format(java.util.Locale.ENGLISH, "%.1f", userRating), // Display userRating with one decimal place
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
