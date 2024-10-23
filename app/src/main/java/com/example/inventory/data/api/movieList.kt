package com.example.inventory.data.api

import okhttp3.OkHttpClient
import okhttp3.Request
import  com.example.inventory.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

const val apiAccessToken = BuildConfig.API_ACCESS_TOKEN

fun apiRequest(url: String, jsonTitle: String) : JSONArray {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url) // Added sort_by parameter
        .get()
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer $apiAccessToken")
        .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string()
    println("results")
    val results : JSONArray = JSONObject(responseBody).getJSONArray((jsonTitle))

    return results
}

fun getMovieQuery(name: String): MutableList<MovieSearchResult>{
    // Gets rid of trailing and leading space and replaces the middle spaces with "%20"
    val queryName = name.trim().replace(" ","%20")

    val movieList = parseMovieList(apiRequest("https://api.themoviedb.org/3/search/movie?query=${queryName}&include_adult=false&language=en-US&page=1", "results"))

    // Sort the movieList by popularity in descending order
    movieList.sortByDescending { it.popularity }

    return movieList
}

fun getTrendingMovies(): List<MovieSearchResult>{
    return parseMovieList(apiRequest("https://api.themoviedb.org/3/trending/movie/week?language=en-US", "results"))
}

// Does it pull all this data for each movie in the search result?
// If so, we should only pull the id, title, poster, and popularity for the results.
// Then get all of the extensive data for a movie once the user has selected it
// maybe have a MovieSearchResult class and a MovieDetailed class
fun parseMovieList(movies: JSONArray): MutableList<MovieSearchResult>{
    val movieAttributes: MutableList<MovieSearchResult> = mutableListOf()
    for ( i in 0 until movies.length()){
        val movie = movies.getJSONObject(i)
        val movieToAdd = MovieSearchResult(
            movie.get("id") as Int,
            movie.get("title") as String,
            movie.get("poster_path").toString() ?: "",
            movie.get("popularity") as Double // Include popularity
        )

        //If the movie poster is null then it doesn't show up, to change remove the if and leave the "movieAttributes.add(movieToAdd)" as it is
        if((movie.get("poster_path").toString()?:"") != "null"){
            movieAttributes.add(movieToAdd)
        }

//        println("${movieToAdd}")
    }
    return movieAttributes
}

//Gets list of all genres available in TMDB
fun getGenre(): MutableList<Pair<String, Int>> {
    val genre = apiRequest("https://api.themoviedb.org/3/genre/movie/list?language=en", "genres")
    val movieGenre: MutableList<Pair<String, Int>> = mutableListOf()
    for(i in 0 until genre.length()){
        movieGenre.add(genre.getJSONObject(i).get("name").toString() to (genre.getJSONObject(i).get("id")) as Int)
    }
    return movieGenre
}

fun displayRandomMovie(movie: Pair<String, Int>):Int{
    val queryMovie = apiRequest("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en&page=1&sort_by=popularity.desc&with_genres=${movie.second.toString()}", "results")
    val movie = queryMovie[Random.nextInt(queryMovie.length())]

    return 1
}