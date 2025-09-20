package com.example.filmflow.data.show

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.collections.List

@Entity(tableName = "shows")
data class Show(
    @PrimaryKey val showID: Int,
    val title: String,
    val posterPath: String,
    val seasonCount: Int?,
    val episodeCount: Int?,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val userRating: Float?,
    val genres: List<String>?
)
