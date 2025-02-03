package com.example.inventory.ui.home

import android.icu.text.DecimalFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.inventory.data.api.MovieDetails


// For formatting the TMDB community rating into a nice percentage
@Composable
fun RatingText(movieDetails: MovieDetails?) {
    val rating = movieDetails?.rating

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