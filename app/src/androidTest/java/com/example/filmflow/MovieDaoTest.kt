package com.example.filmflow

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.filmflow.data.AppDatabase
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.runner.RunWith
import android.content.Context
import com.example.filmflow.data.movie.Movie
import com.example.filmflow.data.movie.MovieDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {
    private lateinit var movieDao: MovieDao
    private lateinit var db: AppDatabase
    private val movie1 = Movie(123, "Smile", "t324fdsaf/",
        "2023-09-08", 120, 6.4f,listOf("Horror", "Sci-fi"))
    private val movie2 = Movie(456, "Ball", "y8787433434/",
        "2020-12-30", 146, 7.5f,
        listOf("Comedy", "Drama", "Thriller"))
    private val movie3 = Movie(789, "Harry Potter", "000123241/",
        "2001-10-01", 180, 8.2f,
        listOf("Fantasy"))
    private val movie4 = Movie(123, "Laugh", "t324fdsaf/",
        "2023-09-08", 120, 2.2f,
         listOf("Horror", "Sci-fi"))

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        movieDao = db.movieDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test // insert and retrieve multiple movies
    @Throws(Exception::class)
    fun insertAndRetrieveAllMovies() = runBlocking {
        movieDao.insert(movie1)
        movieDao.insert(movie2)
        movieDao.insert(movie3)
        val allMovies = movieDao.getAllMovies().first()
        assertEquals(allMovies[0], movie1)
        assertEquals(allMovies[1], movie2)
        assertEquals(allMovies[2], movie3)
    }

    @Test // insert and retrieve 1 movie
    @Throws(Exception::class)
    fun insertAndRetrieveSingleMovie() = runBlocking {
        movieDao.insert(movie2)
        val singleMovie = movieDao.getMovie(456).first()
        assertEquals(singleMovie, movie2)
    }

    @Test // attempt to insert movie with same id
    @Throws(Exception::class)
    fun insertDuplicateMovieIgnore() = runBlocking {
        movieDao.insert(movie1)
        movieDao.insert(movie4)
        val retrievedMovie = movieDao.getMovie(123).first()
        assertEquals(retrievedMovie?.title, "Smile")
    }

    @Test // update a movie's details
    @Throws(Exception::class)
    fun updateMovieDetails() = runBlocking {
        movieDao.insert(movie1)
        val updatedMovie = Movie(123, "Smile", "t324fdsaf/",
            "2023-09-08", 120, 6.4f,listOf("Horror", "Sci-fi"))
        movieDao.update(updatedMovie)
        val getMovie1 = movieDao.getMovie(123).first()
        assertEquals(getMovie1, updatedMovie)
    }

    @Test // update the user's rating for a movie
    @Throws(Exception::class)
    fun updateUserRating() = runBlocking {
        movieDao.insert(movie1)
        movieDao.updateUserRating(123, 5.69f)
        val getMovie1 = movieDao.getMovie(123).first()
        assertEquals(getMovie1?.userRating, 5.69f)
    }

    @Test // delete a movie
    @Throws(Exception::class)
    fun deleteMovie() = runBlocking {
        movieDao.insert(movie1)
        movieDao.insert(movie3)
        movieDao.delete(movie1)
        movieDao.delete(movie3)
        val allMovies = movieDao.getAllMovies().first()
        assertTrue(allMovies.isEmpty())
    }

    @Test // delete a movie given an id
    @Throws(Exception::class)
    fun deleteMovieByID() = runBlocking {
        movieDao.insert(movie1)
        movieDao.insert(movie3)
        movieDao.deleteMovieByID(123)
        movieDao.deleteMovieByID(789)
        val allMovies = movieDao.getAllMovies().first()
        assertTrue(allMovies.isEmpty())
    }

}