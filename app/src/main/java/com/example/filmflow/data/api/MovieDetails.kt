package com.example.filmflow.data.api

data class MovieDetails (
//    val id: Int,
    val title: String,
    val overview: String, //synopsis
    val posterPath: String, //movie poster
    val releaseDate: String,
    val runtime: Int,
    val audienceRating: Double //vote_average
)