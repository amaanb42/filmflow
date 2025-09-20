package com.example.filmflow.data.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowSearchResult(
    val id: Int,
    val name: String,
    val posterPath: String,
    val popularity: Double
) : Parcelable
