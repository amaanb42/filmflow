package com.example.filmflow

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.filmflow.data.AppDatabase
import com.example.filmflow.data.userlist.UserListDao
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.runner.RunWith
import android.content.Context
import com.example.filmflow.data.userlist.UserList
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class UserListDaoTest {
    private lateinit var userListDao: UserListDao
    private lateinit var db: AppDatabase
    private val list1: UserList = UserList("Completed")
    private val list2: UserList = UserList("In Progress", 2, 3)
    private val list3: UserList = UserList("Completed", 1)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userListDao = db.userListDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test // insert multiple lists and retrieve them
    @Throws(Exception::class)
    fun insertAndRetrieveAllLists() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        val allLists = userListDao.getAllLists().first()
        assertEquals(allLists[0], list1)
        assertEquals(allLists[1], list2)
    }

    @Test // insert one list and retrieve it
    @Throws(Exception::class)
    fun insertAndRetrieveSingleList() = runBlocking {
        userListDao.insert(list1)
        val singleList = userListDao.getList("Completed").first()
        assertEquals(singleList, list1)
    }

    @Test // insert list and update non-PK values
    @Throws(Exception::class)
    fun insertAndUpdateList() = runBlocking {
        userListDao.insert(list1)
        userListDao.update(UserList("Completed", 1, 0))
        val singleList = userListDao.getList("Completed").first()
        assertEquals(singleList, list3)
    }

    @Test // insert multiple lists and delete them
    @Throws(Exception::class)
    fun insertAndDeleteList() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        userListDao.delete(list1)
        userListDao.delete(list2)
        val allLists = userListDao.getAllLists().first()
        assertTrue(allLists.isEmpty())
    }

    @Test // try to insert list with same name, not allowed
    @Throws(Exception::class)
    fun insertDuplicateListIgnore() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list3)
        val retrievedList = userListDao.getList("Completed").first()
        assertEquals(0, retrievedList?.movieCount)
    }

    @Test // delete a list given its name
    @Throws(Exception::class)
    fun insertAndDeleteListByName() = runBlocking {
        userListDao.insert(list1)
        userListDao.deleteListByName("Completed")
        val retrievedList = userListDao.getList("Completed").firstOrNull()
        assertNull(retrievedList)
    }

    @Test // change a specified list's name
    @Throws(Exception::class)
    fun insertAndUpdateListByName() = runBlocking {
        userListDao.insert(list2)
        userListDao.updateListByName("In Progress", "Favorites")
        val allLists = userListDao.getAllLists().first()
        assertEquals("Favorites", allLists[0].listName)
    }

    @Test // test incrementing movie and show counts
    @Throws(Exception::class)
    fun incrementCounts() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        userListDao.insert(UserList("Favorite", 1, 0))
        userListDao.incMovieCount("Completed")
        userListDao.incShowCount("In Progress")
        userListDao.incMovieCount("Favorite")
        userListDao.incShowCount("Favorite")
        val getList1 = userListDao.getList("Completed").firstOrNull()
        val getList2 = userListDao.getList("In Progress").firstOrNull()
        val getList3 = userListDao.getList("Favorite").firstOrNull()
        assertEquals(getList1?.movieCount, 1)
        assertEquals(getList2?.showCount, 4)
        assertEquals(getList3?.movieCount, 2)
        assertEquals(getList3?.showCount, 1)
    }

    @Test // test decrementing movie and show counts
    @Throws(Exception::class)
    fun decrementCounts() = runBlocking {
        userListDao.insert(list1)
        userListDao.insert(list2)
        userListDao.insert(UserList("Favorite", 5, 6))
        userListDao.decMovieCount("Completed")
        userListDao.decShowCount("In Progress")
        userListDao.decMovieCount("Favorite")
        userListDao.decShowCount("Favorite")
        val getList1 = userListDao.getList("Completed").firstOrNull()
        val getList2 = userListDao.getList("In Progress").firstOrNull()
        val getList3 = userListDao.getList("Favorite").firstOrNull()
        assertEquals(getList1?.movieCount, 0)
        assertEquals(getList2?.showCount, 2)
        assertEquals(getList3?.movieCount, 4)
        assertEquals(getList3?.showCount, 5)
    }

}