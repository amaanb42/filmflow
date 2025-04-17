package com.example.inventory.data.api

data class MediaCast(
    val id: Int, // Default value for id
    val realName: String = "Unknown", // Default value for realName
    val characterName: String = "Unknown", // Default value for characterName
    val posterPath: String // Default value for posterPath
)