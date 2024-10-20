package com.example.inventory.data.api

data class MovieDetails (
//    val id: Int,
    val title: String,
    val overview: String, //synopsis
    val posterPath: String, //movie poster
    val releaseDate: String,
    val runtime: Int,
    val rating: Double //vote_average
)