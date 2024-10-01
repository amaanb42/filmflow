package com.example.inventory.data

import androidx.room.Entity
import androidx.room.ForeignKey

/* associative entity between List and Movie ; many-to-many relationship*/
@Entity(tableName = "list_movies",
    primaryKeys = ["listName", "movieID"],
    foreignKeys = [
        ForeignKey(
            entity = UserList::class, // reference to UserList entity
            parentColumns = ["listName"], // PK in UserList
            childColumns = ["listName"], // FK in here, ListMovies
            onDelete = ForeignKey.CASCADE, // cascade deletes and updates from UserList to here
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Movie::class, // reference to Movie entity
            parentColumns = ["movieID"], // PK in Movie
            childColumns = ["movieID"], // FK in here, ListMovies
            onDelete = ForeignKey.CASCADE, // cascade deletes and updates from Movie to here
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ListMovies(
    val listName: String,
    val movieID: Int // TMDB id of a movie
)