package com.example.inventory.data

import androidx.room.TypeConverter

class Converters {

    /* This will convert the genres stored as a single string into a List */
    @TypeConverter
    fun fromStringToList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    /* This is used for storing a list of genres returned from an API call into the database */
    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return list.joinToString(",")
    }
}