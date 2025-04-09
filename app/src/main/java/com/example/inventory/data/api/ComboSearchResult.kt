package com.example.inventory.data.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// data class to hold values from both movie and show lists for SearchRows function in SearchScreen
@Parcelize
data class ComboSearchResult(
    val id: Int,
    val name: String, // Will hold either movie title or show name
    val posterPath: String,
    val popularity: Double,
    val type: MediaType // Add a type property
) : Parcelable

enum class MediaType {
    MOVIE,
    SHOW
}