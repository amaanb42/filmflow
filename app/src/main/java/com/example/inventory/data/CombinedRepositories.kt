package com.example.inventory.data

import com.example.inventory.data.listmovies.ListMovies
import com.example.inventory.data.listshows.ListShows
import com.example.inventory.data.movie.Movie
import com.example.inventory.data.show.Show
import com.example.inventory.data.userlist.UserList
import kotlinx.coroutines.flow.Flow
/* UNSURE IF NECESSARY */
interface UserListRepository {
    fun getAllListsStream(): Flow<List<UserList>>
    fun getListStream(listName: String): Flow<UserList?>
    suspend fun incMovieCount(listName: String)
    suspend fun incShowCount(listName: String)
    suspend fun decMovieCount(listName: String)
    suspend fun decShowCount(listName: String)
    fun getMovieCountStream(listName: String): Flow<Int>
    fun getTotalMovieCount(): Flow<Int>
    suspend fun updateListByName(oldName: String, newName: String)
    suspend fun deleteListByName(listName: String)
    suspend fun insertList(list: UserList)
    suspend fun updateList(list: UserList)
    suspend fun deleteList(list: UserList)
}

interface MovieRepository {
    fun getAllMoviesStream(): Flow<List<Movie>>
    fun getMovieStream(movieID: Int): Flow<Movie?>
    suspend fun updateUserRating(movieID: Int, newRating: Float)
    suspend fun deleteMovieByID(movieID: Int)
    suspend fun insertMovie(movie: Movie)
    suspend fun updateMovie(movie: Movie)
    suspend fun deleteMovie(movie: Movie)
}

interface ShowRepository {
    fun getAllShowsStream(): Flow<List<Show>>
    fun getShowStream(showID: Int): Flow<Show?>
    suspend fun updateUserRating(showID: Int, newRating: Float)
    suspend fun deleteShowByID(showID: Int)
    suspend fun insertShow(show: Show)
    suspend fun updateShow(show: Show)
    suspend fun deleteShow(show: Show)
}

interface ListMoviesRepository {
    fun getMoviesForListStream(listName: String): Flow<List<Movie>>
    fun getListsForMovieStream(movieID: Int): Flow<List<String>>
    suspend fun insertListMovieRelation(listMovieRelation: ListMovies)
    suspend fun updateListMovieRelation(listMovieRelation: ListMovies)
    suspend fun deleteListMovieRelation(listMovieRelation: ListMovies)
}

interface ListShowsRepository {
    fun getShowsForListStream(listName: String): Flow<List<Show>>
    fun getListsForShowStream(showID: Int): Flow<List<String>>
    suspend fun insertListShowRelation(listShowRelation: ListShows)
    suspend fun updateListShowRelation(listShowRelation: ListShows)
    suspend fun deleteListShowRelation(listShowRelation: ListShows)
}