package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserListDao {

    /* Get all lists */
    @Query("SELECT * FROM user_lists ORDER BY listName COLLATE NOCASE ASC")
    fun getAllLists(): Flow<List<UserList>>

    /* Get a list (USE ONLY FOR RETRIEVING MOVIE AND SHOW COUNTS) */
    @Query("SELECT * FROM user_lists WHERE listName=:listName")
    fun getList(listName: String): Flow<UserList?>

    /* Insert a new list */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(list: UserList)

    /* Update a list */
    @Update
    suspend fun update(list: UserList)

    /* Delete a list */
    @Delete
    suspend fun delete(list: UserList)

}