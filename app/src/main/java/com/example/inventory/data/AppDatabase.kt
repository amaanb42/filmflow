package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [UserList::class, Movie::class, Show::class, ListMovies::class, ListShows::class],
    version = 3,
    exportSchema = false
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

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ): AppDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration() // Keep for development without migrations
                    .addCallback(PrepopulateCallback(context, scope))  // Use a custom callback
                    .build()
                Instance = instance
                instance
            }
        }
    }
}

// Custom Callback for Pre-population
class PrepopulateCallback(
    private val context: Context,
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            prepopulateDatabase(context) // Pass the context, not the database instance
        }
    }

    private suspend fun prepopulateDatabase(context: Context) {
        // Now we use the AppContainer to get the repository. This is cleaner.
        val userListRepository = AppDataContainer(context).userListRepository
        userListRepository.insertList(UserList("Watching"))
        userListRepository.insertList(UserList("Planning"))
        userListRepository.insertList(UserList("Completed"))
    }
}