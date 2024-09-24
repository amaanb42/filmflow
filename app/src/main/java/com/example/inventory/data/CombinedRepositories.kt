package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

interface UserListRepository {
    fun getAllListsStream(): Flow<List<UserList>>
    fun getListStream(listName: String): Flow<UserList?>
    suspend fun insertList(list: UserList)
    suspend fun updateList(list: UserList)
    suspend fun delete(list: UserList)
}

interface MovieRepository {
    fun getAllMoviesStream(): Flow<List<Movie>>
    fun getMovieStream(movieID: Int): Flow<Movie?>
    suspend fun insertMovie(movie: Movie)
    suspend fun updateMovie(movie: Movie)
    suspend fun deleteMovie(movie: Movie)
}

interface ShowRepository {
    fun getAllShowsStream(): Flow<List<Show>>
    fun getShowStream(showID: Int): Flow<Show?>
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