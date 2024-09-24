package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    /* Get all movies */
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<Movie>>

    /* Get a movie */
    @Query("SELECT * FROM movies WHERE movieID = :movieID")
    fun getMovie(movieID: Int): Flow<Movie>

    /* Insert a new movie */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movie: Movie)

    /* Update a movie's details */
    @Update
    suspend fun update(movie: Movie)

    /* Delete a movie */
    @Delete
    suspend fun delete(movie: Movie)

}