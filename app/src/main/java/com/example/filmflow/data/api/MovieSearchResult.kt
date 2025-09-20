package com.example.filmflow.data.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieSearchResult(
    val id: Int,
    val title: String,
    val posterPath: String,
    val popularity: Double
) : Parcelable
