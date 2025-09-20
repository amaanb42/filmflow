package com.example.filmflow.data.listmovies

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.filmflow.data.movie.Movie
import kotlinx.coroutines.flow.Flow

@Dao
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listMovieRelation: ListMovies) : Long //returns id

    /* Update list-movie relation */
    @Update
    suspend fun update(listMovieRelation: ListMovies)

    /* Delete list-movie relation */
    @Query("DELETE FROM list_movies WHERE listName = :listName AND movieID = :movieID")
    suspend fun deleteByListNameAndMovieId(listName: String, movieID: Int)

}