package com.example.inventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.collections.List

@Entity(tableName = "shows")
data class Show(
    @PrimaryKey val showID: Int,
    val title: String,
    val overview: String?,
    val creator: String?,
    val posterPath: String,
    val seasonCount: Int?,
    val episodeCount: Int?,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val genres: List<String>?
)
