package com.example.inventory.data.api

data class ShowDetails (
    val showID: Int,
    val title: String,
    val posterPath: String,
    val seasonCount: Int?,
    val episodeCount: Int?,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val userRating: Float?,
    val genres: List<String>?
)