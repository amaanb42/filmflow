package com.example.filmflow.data

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

    // Create the database instance ONCE.
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }


    override val userListRepository: UserListRepository by lazy {
        OfflineUserListRepository(database.userListDao()) // Pass the instance
    }
    override val movieRepository: MovieRepository by lazy {
        OfflineMovieRepository(database.movieDao())  // Pass the instance
    }
    override val listMoviesRepository: ListMoviesRepository by lazy {
        OfflineListMoviesRepository(database.listMoviesDao()) // Pass the instance
    }
    override val showRepository: ShowRepository by lazy {
        OfflineShowRepository(database.showDao())  // Pass the instance
    }
    override val listShowsRepository: ListShowsRepository by lazy {
        OfflineListShowsRepository(database.listShowsDao())  // Pass the instance
    }
}
