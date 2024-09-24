package com.example.inventory.data

import androidx.room.Entity

/* associative entity between List and Movie ; many-to-many relationship*/
@Entity(tableName = "list_movies", primaryKeys = ["listName", "movieID"])
data class ListMovies(
    val listName: String,
    val movieID: Int // TMDB id of a movie
)