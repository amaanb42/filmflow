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
    version = 4, // IMPORTANT: Increment the version number!
    exportSchema = true // Good practice to export schema
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userListDao(): UserListDao
    abstract fun movieDao(): MovieDao
    abstract fun showDao(): ShowDao
    abstract fun listMoviesDao(): ListMoviesDao
    abstract fun listShowsDao(): ListShowsDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Database name (should match asset file name, without extension)
                )
                    .createFromAsset("databases/app_database.db") // Use createFromAsset
                    //.fallbackToDestructiveMigration() // REMOVE THIS FOR PRODUCTION
                    .build()
                Instance = instance
                instance
            }
        }
    }
}

