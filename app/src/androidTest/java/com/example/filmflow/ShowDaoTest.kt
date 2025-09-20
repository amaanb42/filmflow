package com.example.filmflow

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.filmflow.data.AppDatabase
import com.example.filmflow.data.show.Show
import com.example.filmflow.data.show.ShowDao
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
class ShowDaoTest {
    private lateinit var showDao: ShowDao
    private lateinit var db: AppDatabase
    private val show1 = Show(
        showID = 123,
        title = "GoT",
        posterPath = "Dragoons.",
        seasonCount = 7,
        episodeCount = 100,
        firstAirDate = "2011-07-21",
        lastAirDate = "2017-11-01",
        userRating = null,
        genres = listOf("Fantasy", "Fiction", "Adventure")
    )
    private val show2 = Show(
        showID = 456,
        title = "Rob & Big",
        posterPath = "Rad.",
        seasonCount = 5,
        episodeCount = 80,
        firstAirDate = "2005-01-21",
        lastAirDate = "2010-09-20",
        userRating = null,
        genres = listOf("Reality")
    )
    private val show3 = Show(
        showID = 789,
        title = "Barry",
        posterPath = "IDK",
        seasonCount = 6,
        episodeCount = 99,
        firstAirDate = "2014-04-20",
        lastAirDate = "2020-05-17",
        userRating = null,
        genres = listOf("Fiction", "Drama")
    )
    private val show4 = Show(
        showID = 123,
        title = "HoTD",
        posterPath = "Dragoons.",
        seasonCount = 7,
        episodeCount = 100,
        firstAirDate = "2011-07-21",
        lastAirDate = "2017-11-01",
        userRating = null,
        genres = listOf("Fantasy", "Fiction", "Adventure")
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        showDao = db.showDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test // insert and retrieve multiple shows
    @Throws(Exception::class)
    fun insertAndRetrieveAllShows() = runBlocking {
        showDao.insert(show1)
        showDao.insert(show2)
        showDao.insert(show3)
        val allMovies = showDao.getAllShows().first()
        assertEquals(allMovies[0], show1)
        assertEquals(allMovies[1], show2)
        assertEquals(allMovies[2], show3)
    }

    @Test // insert and retrieve 1 show
    @Throws(Exception::class)
    fun insertAndRetrieveSingleMovie() = runBlocking {
        showDao.insert(show2)
        val singleMovie = showDao.getShow(456).first()
        assertEquals(singleMovie, show2)
    }

    @Test // attempt to insert show with same id
    @Throws(Exception::class)
    fun insertDuplicateMovieIgnore() = runBlocking {
        showDao.insert(show1)
        showDao.insert(show4)
        val retrievedShow = showDao.getShow(123).first()
        assertEquals(retrievedShow?.title, "GoT")
    }

    @Test // update a show's details
    @Throws(Exception::class)
    fun updateMovieDetails() = runBlocking {
        showDao.insert(show1)
        val updatedShow = Show(
            showID = 123,
            title = "GoT",
            posterPath = "Dragoons and fire.",
            seasonCount = 7,
            episodeCount = 89,
            firstAirDate = "2011-07-21",
            lastAirDate = "2017-11-01",
            userRating = null,
            genres = listOf("Fantasy")
        )
        showDao.update(updatedShow)
        val getShow1 = showDao.getShow(123).first()
        assertEquals(getShow1, updatedShow)
    }

    @Test // update the user's rating for a show
    @Throws(Exception::class)
    fun updateUserRating() = runBlocking {
        showDao.insert(show1)
        showDao.updateUserRating(123, 7.00f)
        val getShow1 = showDao.getShow(123).first()
        assertEquals(getShow1?.userRating, 7.00f)
    }

    @Test // delete a show
    @Throws(Exception::class)
    fun deleteShow() = runBlocking {
        showDao.insert(show1)
        showDao.insert(show3)
        showDao.delete(show1)
        showDao.delete(show3)
        val allShows = showDao.getAllShows().first()
        assertTrue(allShows.isEmpty())
    }

    @Test // delete a show given an id
    @Throws(Exception::class)
    fun deleteShowByID() = runBlocking {
        showDao.insert(show1)
        showDao.insert(show3)
        showDao.deleteShowByID(123)
        showDao.deleteShowByID(789)
        val allShows = showDao.getAllShows().first()
        assertTrue(allShows.isEmpty())
    }

}