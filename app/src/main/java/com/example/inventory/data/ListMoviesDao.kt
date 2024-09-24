package com.example.inventory.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface ListMoviesDao {

    /* Get movies in a list; join list_movies with movies given a list name*/
    @Query("SELECT movies.* " +
            "FROM list_movies, movies " +
            "WHERE list_movies.listName = :listName AND list_movies.movieID = movies.movieID"
    )
    fun getMoviesForList(listName: String): Flow<List<Movie>> //return only the Movie objects

    /* Get lists that a movie is in */
    @Query("SELECT listName " +
            "FROM list_movies " +
            "WHERE movieID = :movieID"
    )
    fun getListsForMovie(movieID: Int): Flow<List<String>> //return list of strings (the list names)

    /* Insert new list-movie relation */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(listMovieRelation: ListMovies)

    /* Update list-movie relation */
    @Update
    suspend fun update(listMovieRelation: ListMovies)

    /* Delete list-movie relation */
    @Delete
    suspend fun delete(listMovieRelation: ListMovies)

}