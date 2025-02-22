package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.AppDatabase
import com.example.inventory.data.listshows.ListShows
import com.example.inventory.data.listshows.ListShowsDao
import com.example.inventory.data.show.Show
import com.example.inventory.data.show.ShowDao
import com.example.inventory.data.userlist.UserList
import com.example.inventory.data.userlist.UserListDao
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
class ListShowsDaoTest {
    private lateinit var userListDao: UserListDao
    private lateinit var listShowsDao: ListShowsDao
    private lateinit var showDao: ShowDao
    private lateinit var db: AppDatabase
    private val list1: UserList = UserList("Completed")
    private val list2: UserList = UserList("In Progress")
    private val show1 = Show(
        showID = 123,
        title = "GoT",
        posterPath = "/432er3jkd",
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
        posterPath = "/432er3jkd",
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
        posterPath = "/432er3jkd",
        seasonCount = 6,
        episodeCount = 99,
        firstAirDate = "2014-04-20",
        lastAirDate = "2020-05-17",
        userRating = null,
        genres = listOf("Fiction", "Drama")
    )
    private val listShowPair1 = ListShows(1,"Completed", 123)
    private val listShowPair2 = ListShows(2,"In Progress", 456)
    private val listShowPair3 = ListShows(3,"Completed", 789)
    private val listShowPair4 = ListShows(4,"In Progress", 123)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userListDao = db.userListDao()
        listShowsDao = db.listShowsDao()
        showDao = db.showDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test // insert and retrieve shows given list name
    @Throws(Exception::class)
    fun getShowsForList() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        showDao.insert(show1)
        showDao.insert(show2)
        showDao.insert(show3)
        listShowsDao.insert(listShowPair1)
        listShowsDao.insert(listShowPair2)
        listShowsDao.insert(listShowPair3)
        val retrievedPairs = listShowsDao.getShowsForList("Completed").first()
        assertEquals(retrievedPairs[0], show1)
        assertEquals(retrievedPairs[1], show3)
    }

    @Test // insert and retrieve lists given show id
    @Throws(Exception::class)
    fun getListsForShow() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        showDao.insert(show1)
        showDao.insert(show2)
        showDao.insert(show3)
        listShowsDao.insert(listShowPair1)
        listShowsDao.insert(listShowPair2)
        listShowsDao.insert(listShowPair3)
        listShowsDao.insert(listShowPair4)
        val retrievedPairs = listShowsDao.getListsForShow(123).first()
        assertEquals(retrievedPairs[0], "Completed")
        assertEquals(retrievedPairs[1], "In Progress")
    }

    @Test // insert list-show pair, then update list name to test if pairs get updated
    @Throws(Exception::class)
    fun updateListName() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        showDao.insert(show1)
        showDao.insert(show2)
        showDao.insert(show3)
        listShowsDao.insert(listShowPair1)
        listShowsDao.insert(listShowPair2)
        listShowsDao.insert(listShowPair3)
        listShowsDao.insert(listShowPair4)
        userListDao.updateListByName("Completed", "Watched")
        val retrievedPair1 = listShowsDao.getListsForShow(123).first()
        val retrievedPair2 = listShowsDao.getListsForShow(789).first()
        assertEquals(retrievedPair1[0], "Watched")
        assertEquals(retrievedPair2[0], "Watched")
    }

    @Test // insert pairs, then delete list to see if pairs are deleted
    @Throws(Exception::class)
    fun deleteList() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        showDao.insert(show1)
        showDao.insert(show2)
        showDao.insert(show3)
        listShowsDao.insert(listShowPair1)
        listShowsDao.insert(listShowPair2)
        listShowsDao.insert(listShowPair3)
        listShowsDao.insert(listShowPair4)
        userListDao.deleteListByName("Completed")
        val retrievedPairs = listShowsDao.getShowsForList("Completed").first()
        assertTrue(retrievedPairs.isEmpty())
    }

    @Test // insert pairs, then delete show to see if pairs are deleted
    @Throws(Exception::class)
    fun deleteMovie() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        showDao.insert(show1)
        showDao.insert(show2)
        showDao.insert(show3)
        listShowsDao.insert(listShowPair1)
        listShowsDao.insert(listShowPair2)
        listShowsDao.insert(listShowPair3)
        listShowsDao.insert(listShowPair4)
        showDao.deleteShowByID(123)
        val retrievedPairs = listShowsDao.getListsForShow(123).first()
        assertTrue(retrievedPairs.isEmpty())
    }
}