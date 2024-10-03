package com.example.inventory.data.userlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="user_lists")
data class UserList(
    @PrimaryKey val listName: String,
    val movieCount: Int = 0, //default the counts
    val showCount: Int = 0
)