package com.example.inventory.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val userListRepository: UserListRepository
    val movieRepository: MovieRepository
    val listMoviesRepository: ListMoviesRepository
    val showRepository: ShowRepository
    val listShowsRepository: ListShowsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val userListRepository: UserListRepository by lazy {
        OfflineUserListRepository(AppDatabase.getDatabase(context).userListDao())
    }
    override val movieRepository: MovieRepository by lazy {
        OfflineMovieRepository(AppDatabase.getDatabase(context).movieDao())
    }
    override val listMoviesRepository: ListMoviesRepository by lazy {
        OfflineListMoviesRepository(AppDatabase.getDatabase(context).listMoviesDao())
    }
    override val showRepository: ShowRepository by lazy {
        OfflineShowRepository(AppDatabase.getDatabase(context).showDao())
    }
    override val listShowsRepository: ListShowsRepository by lazy {
        OfflineListShowsRepository(AppDatabase.getDatabase(context).listShowsDao())
    }
}
