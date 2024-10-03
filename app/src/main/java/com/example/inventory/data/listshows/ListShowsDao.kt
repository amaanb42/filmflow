package com.example.inventory.data.listshows

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.inventory.data.show.Show
import kotlinx.coroutines.flow.Flow

@Dao
interface ListShowsDao {

    /* Get shows in a list; join list_shows with shows given a list name*/
    @Query("SELECT shows.* " +
            "FROM list_shows, shows " +
            "WHERE list_shows.listName = :listName AND list_shows.showID = shows.showID"
    )
    fun getShowsForList(listName: String): Flow<List<Show>> //return only the Show objects

    /* Get lists that a show is in */
    @Query("SELECT listName " +
            "FROM list_shows " +
            "WHERE showID = :showID"
    )
    fun getListsForShow(showID: Int): Flow<List<String>> //return list of strings (the list names)

    /* Insert new list-show relation */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(listShowRelation: ListShows)

    /* Update list-show relation */
    @Update
    suspend fun update(listShowRelation: ListShows)

    /* Delete list-show relation */
    @Delete
    suspend fun delete(listShowRelation: ListShows)

}