package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

class OfflineUserListRepository(private val userListDao: UserListDao) : UserListRepository {
    override fun getAllListsStream(): Flow<List<UserList>> = userListDao.getAllLists()
    override fun getListStream(listName: String): Flow<UserList?> = userListDao.getList(listName)
    override suspend fun incMovieCount(listName: String) = userListDao.incMovieCount(listName)
    override suspend fun incShowCount(listName: String) = userListDao.incShowCount(listName)
    override suspend fun decMovieCount(listName: String) = userListDao.decMovieCount(listName)
    override suspend fun decShowCount(listName: String) = userListDao.decShowCount(listName)
    override suspend fun updateListByName(oldName: String, newName: String) = userListDao.updateListByName(oldName, newName)
    override suspend fun deleteListByName(listName: String) = userListDao.deleteListByName(listName)
    override suspend fun insertList(list: UserList) = userListDao.insert(list)
    override suspend fun updateList(list: UserList) = userListDao.update(list)
    override suspend fun deleteList(list: UserList) = userListDao.delete(list)
}

class OfflineMovieRepository(private val movieDao: MovieDao) : MovieRepository {
    override fun getAllMoviesStream(): Flow<List<Movie>> = movieDao.getAllMovies()
    override fun getMovieStream(movieID: Int): Flow<Movie?> = movieDao.getMovie(movieID)
    override suspend fun updateUserRating(movieID: Int, newRating: Float) = movieDao.updateUserRating(movieID, newRating)
    override suspend fun deleteMovieByID(movieID: Int) = movieDao.deleteMovieByID(movieID)
    override suspend fun insertMovie(movie: Movie) = movieDao.insert(movie)
    override suspend fun updateMovie(movie: Movie) = movieDao.update(movie)
    override suspend fun deleteMovie(movie: Movie) = movieDao.delete(movie)
}

class OfflineShowRepository(private val showDao: ShowDao) : ShowRepository {
    override fun getAllShowsStream(): Flow<List<Show>> = showDao.getAllShows()
    override fun getShowStream(showID: Int): Flow<Show?> = showDao.getShow(showID)
    override suspend fun updateUserRating(showID: Int, newRating: Float) = showDao.updateUserRating(showID, newRating)
    override suspend fun deleteShowByID(showID: Int) = showDao.deleteShowByID(showID)
    override suspend fun insertShow(show: Show) = showDao.insert(show)
    override suspend fun updateShow(show: Show) = showDao.update(show)
    override suspend fun deleteShow(show: Show) = showDao.delete(show)
}

class OfflineListMoviesRepository(private val listMoviesDao: ListMoviesDao) : ListMoviesRepository {
    override fun getMoviesForListStream(listName: String): Flow<List<Movie>> = listMoviesDao.getMoviesForList(listName)
    override fun getListsForMovieStream(movieID: Int): Flow<List<String>> = listMoviesDao.getListsForMovie(movieID)
    override suspend fun insertListMovieRelation(listMovieRelation: ListMovies) = listMoviesDao.insert(listMovieRelation)
    override suspend fun updateListMovieRelation(listMovieRelation: ListMovies) = listMoviesDao.update(listMovieRelation)
    override suspend fun deleteListMovieRelation(listMovieRelation: ListMovies) = listMoviesDao.delete(listMovieRelation)
}

class OfflineListShowsRepository(private val listShowsDao: ListShowsDao) : ListShowsRepository {
    override fun getShowsForListStream(listName: String): Flow<List<Show>> = listShowsDao.getShowsForList(listName)
    override fun getListsForShowStream(showID: Int): Flow<List<String>> = listShowsDao.getListsForShow(showID)
    override suspend fun insertListShowRelation(listShowRelation: ListShows) = listShowsDao.insert(listShowRelation)
    override suspend fun updateListShowRelation(listShowRelation: ListShows) = listShowsDao.update(listShowRelation)
    override suspend fun deleteListShowRelation(listShowRelation: ListShows) = listShowsDao.delete(listShowRelation)
}