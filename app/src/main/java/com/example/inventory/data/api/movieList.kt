package com.example.inventory.data.api

import okhttp3.OkHttpClient
import okhttp3.Request
import  com.example.inventory.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import com.example.inventory.data.Movie

fun getMovieQuery(name: String): MutableList<Movie>{
    val client = OkHttpClient()
    val api_access_token = BuildConfig.API_ACCESS_TOKEN

    // Gets rid of trailing and leading space and replaces the middle spaces with "%20"
    val queryName = name.trim().replace(" ","%20")

    val request = Request.Builder()
        .url("https://api.themoviedb.org/3/search/movie?query=${queryName}&include_adult=false&language=en-US&page=1")
        .get()
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer ${api_access_token}")
        .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string()
    val results : JSONArray = JSONObject(responseBody).getJSONArray(("results"))
//    println(results)
    return parseMovieList(results)
}

fun parseMovieList(movies: JSONArray): MutableList<Movie>{
    val movieAttributes: MutableList<Movie> = mutableListOf()
    for ( i in 0 until movies.length()){
        val movie = movies.getJSONObject(i)
        val movieToAdd = Movie(movie.get("id") as Int, movie.get("title") as String, movie.get("overview") as String,
            movie.get("poster_path").toString()?:"", movie.get("release_date") as String, movie.get("vote_average") as Double)
        movieAttributes.add(movieToAdd)
//        println("${movieToAdd}")
    }
    return movieAttributes
}