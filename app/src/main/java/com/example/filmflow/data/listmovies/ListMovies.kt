package com.example.filmflow.data.listmovies

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.filmflow.data.movie.Movie
import com.example.filmflow.data.userlist.UserList

/* associative entity between List and Movie ; many-to-many relationship*/
@Entity(
    tableName = "list_movies",
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
    ],
    indices = [Index("movieID"), Index("listName")]
)
data class ListMovies(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated PK
    val listName: String,
    val movieID: Int // TMDB id of a movie
)