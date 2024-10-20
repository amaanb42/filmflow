package com.example.inventory.data.api

import okhttp3.OkHttpClient
import okhttp3.Request
import  com.example.inventory.BuildConfig
import org.json.JSONArray
import org.json.JSONObject

const val apiAccessToken = BuildConfig.API_ACCESS_TOKEN

fun apiRequest(url: String): JSONObject { // Changed return type to JSONObject
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer $apiAccessToken")
    .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string()
    return JSONObject(responseBody) // Return the whole JSON object
}

fun getMovieQuery(name: String): MutableList<MovieSearchResult> {
    // Gets rid of trailing and leading space and replaces the middle spaces with "%20"
    val queryName = name.trim().replace(" ", "%20")

    val movieJson = apiRequest("https://api.themoviedb.org/3/search/movie?query=${queryName}&include_adult=false&language=en-US&page=1")
    val resultsArray = movieJson.getJSONArray("results") // Extract the "results" array

    val movieList = parseMovieList(resultsArray)
    // Sort the movieList by popularity in descending order
    movieList.sortByDescending { it.popularity }

    return movieList
}

fun getTrendingMovies(): List<MovieSearchResult> {
    val trendingMoviesJson = apiRequest("https://api.themoviedb.org/3/trending/movie/week?language=en-US")
    return parseMovieList(trendingMoviesJson.getJSONArray("results"))
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

fun parseMovieDetails(movie: JSONObject): MovieDetails {
    return MovieDetails(
        movie.getString("title"),
        movie.getString("overview"),
        movie.getString("poster_path") ?: "",
        movie.getString("release_date") ?: "",
        movie.getInt("runtime"),
        movie.getDouble("vote_average")
    )
}


fun getDetailsFromID(id: Int): MovieDetails {
    val movieJson = apiRequest("https://api.themoviedb.org/3/movie/${id}?language=en-US")
    return parseMovieDetails(movieJson) // Parse the JSON object here
}