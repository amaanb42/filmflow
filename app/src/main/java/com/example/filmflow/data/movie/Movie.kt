package com.example.filmflow.data.movie

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.collections.List

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val movieID: Int,
    val title: String, // must stay
    val posterPath: String, // keep this
    val releaseDate: String?, // must stay
    val runtime: Int?, // must stay
    val userRating: Float?, // must stay
    val genres: List<String>? //need to either use TypeConverter or make a separate table
)