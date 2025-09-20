package com.example.filmflow

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.filmflow.data.AppDatabase
import com.example.filmflow.data.listmovies.ListMovies
import com.example.filmflow.data.listmovies.ListMoviesDao
import com.example.filmflow.data.movie.Movie
import com.example.filmflow.data.movie.MovieDao
import com.example.filmflow.data.userlist.UserList
import com.example.filmflow.data.userlist.UserListDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class ListMoviesDaoTest {
    private lateinit var userListDao: UserListDao
    private lateinit var listMoviesDao: ListMoviesDao
    private lateinit var movieDao: MovieDao
    private lateinit var db: AppDatabase
    private val list1: UserList = UserList("Completed")
    private val list2: UserList = UserList("In Progress")
    private val movie1 = Movie(123, "Smile", "t324fdsaf/",
        "2023-09-08", 120, 6.4f,listOf("Horror", "Sci-fi"))
    private val movie2 = Movie(456, "Ball", "y8787433434/",
        "2020-12-30", 146, 7.5f,
         listOf("Comedy", "Drama", "Thriller"))
    private val movie3 = Movie(789, "Harry Potter", "000123241/",
        "2001-10-01", 180, 8.2f,
         listOf("Fantasy"))
    private val listMoviePair1 = ListMovies(1,"Completed", 123)
    private val listMoviePair2 = ListMovies(2,"In Progress", 456)
    private val listMoviePair3 = ListMovies(3,"Completed", 789)
    private val listMoviePair4 = ListMovies(4,"In Progress", 123)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userListDao = db.userListDao()
        listMoviesDao = db.listMoviesDao()
        movieDao = db.movieDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test // insert and retrieve movies given list name
    @Throws(Exception::class)
    fun getMoviesForList() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        movieDao.insert(movie1)
        movieDao.insert(movie2)
        movieDao.insert(movie3)
        listMoviesDao.insert(listMoviePair1)
        listMoviesDao.insert(listMoviePair2)
        listMoviesDao.insert(listMoviePair3)
        val retrievedPairs = listMoviesDao.getMoviesForList("Completed").first()
        assertEquals(retrievedPairs[0], movie1)
        assertEquals(retrievedPairs[1], movie3)
    }

    @Test // insert and retrieve lists given movie id
    @Throws(Exception::class)
    fun getListsForMovie() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        movieDao.insert(movie1)
        movieDao.insert(movie2)
        movieDao.insert(movie3)
        listMoviesDao.insert(listMoviePair1)
        listMoviesDao.insert(listMoviePair2)
        listMoviesDao.insert(listMoviePair3)
        listMoviesDao.insert(listMoviePair4)
        val retrievedPairs = listMoviesDao.getListsForMovie(123).first()
        assertEquals(retrievedPairs[0], "Completed")
        assertEquals(retrievedPairs[1], "In Progress")
    }

    @Test // insert list-movie pair, then update list name to test if pairs get updated
    @Throws(Exception::class)
    fun updateListName() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        movieDao.insert(movie1)
        movieDao.insert(movie2)
        movieDao.insert(movie3)
        listMoviesDao.insert(listMoviePair1)
        listMoviesDao.insert(listMoviePair2)
        listMoviesDao.insert(listMoviePair3)
        listMoviesDao.insert(listMoviePair4)
        userListDao.updateListByName("Completed", "Watched")
        val retrievedPair1 = listMoviesDao.getListsForMovie(123).first()
        val retrievedPair2 = listMoviesDao.getListsForMovie(789).first()
        assertEquals(retrievedPair1[0], "Watched")
        assertEquals(retrievedPair2[0], "Watched")
    }

    @Test // insert pairs, then delete list to see if pairs are deleted
    @Throws(Exception::class)
    fun deleteList() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        movieDao.insert(movie1)
        movieDao.insert(movie2)
        movieDao.insert(movie3)
        listMoviesDao.insert(listMoviePair1)
        listMoviesDao.insert(listMoviePair2)
        listMoviesDao.insert(listMoviePair3)
        listMoviesDao.insert(listMoviePair4)
        userListDao.deleteListByName("Completed")
        val retrievedPairs = listMoviesDao.getMoviesForList("Completed").first()
        assertTrue(retrievedPairs.isEmpty())
    }

    @Test // insert pairs, then delete movie to see if pairs are deleted
    @Throws(Exception::class)
    fun deleteMovie() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        movieDao.insert(movie1)
        movieDao.insert(movie2)
        movieDao.insert(movie3)
        listMoviesDao.insert(listMoviePair1)
        listMoviesDao.insert(listMoviePair2)
        listMoviesDao.insert(listMoviePair3)
        listMoviesDao.insert(listMoviePair4)
        movieDao.deleteMovieByID(123)
        val retrievedPairs = listMoviesDao.getListsForMovie(123).first()
        assertTrue(retrievedPairs.isEmpty())
    }
}