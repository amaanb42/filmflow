package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.inventory.data.listmovies.ListMovies
import com.example.inventory.data.listmovies.ListMoviesDao
import com.example.inventory.data.listshows.ListShows
import com.example.inventory.data.listshows.ListShowsDao
import com.example.inventory.data.movie.Movie
import com.example.inventory.data.movie.MovieDao
import com.example.inventory.data.show.Show
import com.example.inventory.data.show.ShowDao
import com.example.inventory.data.userlist.UserList
import com.example.inventory.data.userlist.UserListDao

@Database(
    entities = [UserList::class, Movie::class, Show::class, ListMovies::class, ListShows::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun userListDao(): UserListDao
    abstract fun movieDao(): MovieDao
    abstract fun showDao(): ShowDao
    abstract fun listMoviesDao(): ListMoviesDao
    abstract fun listShowsDao(): ListShowsDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}