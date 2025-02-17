package com.example.inventory.data.movie

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
    fun getMovie(movieID: Int): Flow<Movie?>

    /* Update user's rating */
    @Query("UPDATE movies SET userRating = :newRating WHERE movieID = :movieID")
    suspend fun updateUserRating(movieID: Int, newRating: Float)

    /* Delete movie by id */
    @Query("DELETE FROM movies " +
            "WHERE movieID = :movieID")
    suspend fun deleteMovieByID(movieID: Int)

    /* Insert a new movie */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    /* Update a movie's details */
    @Update
    suspend fun update(movie: Movie)

    /* Delete a movie */
    @Delete
    suspend fun delete(movie: Movie)

}