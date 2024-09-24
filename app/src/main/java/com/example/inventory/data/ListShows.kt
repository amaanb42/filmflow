package com.example.inventory.data

import androidx.room.Entity
import androidx.room.ForeignKey

/* associative entity between Lists and Shows ; many-to-many relationship*/
@Entity(tableName = "list_shows",
    primaryKeys = ["listName", "showID"],
    foreignKeys = [
        ForeignKey(
            entity = UserList::class, // reference to UserList entity
            parentColumns = ["listName"], // PK in UserList
            childColumns = ["listName"], // FK in here, ListMovies
            onDelete = ForeignKey.CASCADE, // cascade deletes and updates from UserList to here
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Show::class, // reference to Show entity
            parentColumns = ["showID"], // PK in Show
            childColumns = ["showID"], // FK in here, ListShows
            onDelete = ForeignKey.CASCADE, // cascade deletes and updates from Show to here
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ListShows(
    val listName: String,
    val showID: Int // TMDB id of a show
)
