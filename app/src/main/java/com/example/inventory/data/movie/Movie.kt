package com.example.inventory.data.movie

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.collections.List

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val movieID: Int,
    val title: String, // must stay
    val overview: String?, // can remove this
    val director: String?, // can remove this
    val posterPath: String, // can probably remove this?
    val releaseDate: String?, // must stay
    val runtime: Int?, // must stay
    val userRating: Float?, // must stay
    val genres: List<String>? //need to either use TypeConverter or make a separate table
)