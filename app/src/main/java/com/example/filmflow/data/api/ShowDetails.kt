package com.example.filmflow.data.api

data class ShowDetails (
    //val showID: Int,
    val title: String,
    val overview: String, //synopsis
    val posterPath: String,
    val seasonCount: Int?,
    val episodeCount: Int?,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val audienceRating: Double,
    val genres: List<String>?
)