package com.example.inventory.data.userlist

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
    @Query("SELECT * " +
            "FROM user_lists " +
            "ORDER BY listName " +
            "COLLATE NOCASE ASC")
    fun getAllLists(): Flow<List<UserList>>

    /* Get a list (USE ONLY FOR RETRIEVING MOVIE AND SHOW COUNTS) */
    @Query("SELECT * " +
            "FROM user_lists " +
            "WHERE listName = :listName")
    fun getList(listName: String): Flow<UserList?>

    /* Increment a list's movie count */
    @Query("UPDATE user_lists " +
            "SET movieCount = movieCount + 1 " +
            "WHERE listName = :listName")
    suspend fun incMovieCount(listName: String)

    /* Increment a list's show count */
    @Query("UPDATE user_lists " +
            "SET showCount = showCount + 1 " +
            "WHERE listName = :listName")
    suspend fun incShowCount(listName: String)

    /* Decrement a list's movie count */
    @Query("UPDATE user_lists " +
            "SET movieCount = movieCount - 1 " +
            "WHERE listName = :listName AND movieCount > 0")
    suspend fun decMovieCount(listName: String)

    /* Decrement a list's show count */
    @Query("UPDATE user_lists " +
            "SET showCount = showCount - 1 " +
            "WHERE listName = :listName AND showCount > 0")
    suspend fun decShowCount(listName: String)

    /* Update a specified list's name, cascades update to list_movies and list_shows bc of FK constraints */
    @Query("UPDATE user_lists " +
            "SET listName = :newName " +
            "WHERE listName = :oldName")
    suspend fun updateListByName(oldName: String, newName: String)

    /* Delete a specified list, cascades delete to list_movies and list_shows bc of FK constraints */
    @Query("DELETE FROM user_lists " +
            "WHERE listName = :listName")
    suspend fun deleteListByName(listName: String)

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