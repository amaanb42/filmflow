package com.example.inventory.data.api

data class MovieDetails (
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val rating: Double,
    val popularity: Double
)