package com.example.inventory.data

import androidx.room.Entity

/* associative entity between Lists and Shows ; many-to-many relationship*/
@Entity(tableName = "list_shows", primaryKeys = ["listName", "showID"])
data class ListShows(
    val listName: String,
    val showID: Int // TMDB id of a show
)
