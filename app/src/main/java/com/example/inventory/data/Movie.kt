package com.example.inventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.collections.List

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val movieID: Int,
    val title: String,
    val overview: String?,
    val director: String?,
    val posterPath: String,
    val releaseDate: String?,
    val runtime: Int?,
    val userRating: Float?,
    val genres: List<String>? //need to either use TypeConverter or make a separate table
)