package com.example.inventory.data.api

import android.util.Log
import com.example.inventory.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import kotlin.random.Random

const val apiAccessToken = BuildConfig.API_ACCESS_TOKEN

fun apiRequest(url: String): JSONObject? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer $apiAccessToken")
        .build()

    try {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        return if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            JSONObject(responseBody)
        } else {
            Log.e("API Request", "Error: ${response.code} - ${response.message}")
            null
        }
    } catch (e: ConnectException) {
        Log.e("API Request", "Network error: No internet connection", e)
        return null
    } catch (e: UnknownHostException) {
        Log.e("API Request", "Network error: Unable to reach server", e)
        return null
    } catch (e: IOException) {
        Log.e("API Request", "IO error during API request", e)
        return null
    } catch (e: Exception) {
        Log.e("API Request", "Unknown error during API request", e)
        return null
    }
}

fun getMovieQuery(name: String): MutableList<MovieSearchResult> {
    // Gets rid of trailing and leading space and replaces the middle spaces with "%20"
    val queryName = name.trim().replace(" ", "%20")

    val movieJson = apiRequest("https://api.themoviedb.org/3/search/movie?query=${queryName}&include_adult=false&language=en-US&page=1")

    // Handle null response
    val resultsArray = movieJson?.getJSONArray("results") ?: JSONArray()

    val movieList = parseMovieList(resultsArray)
    // Sort the movieList by popularity in descending order
    movieList.sortByDescending { it.popularity }

    return movieList
}

fun getTrendingMovies(): List<MovieSearchResult> {
    val trendingMoviesJson = apiRequest("https://api.themoviedb.org/3/trending/movie/week?language=en-US")
    val resultsArray = trendingMoviesJson?.getJSONArray("results") ?: JSONArray()
    return parseMovieList(resultsArray)
}

fun getNowPlayingMovies(): List<MovieSearchResult>{
    val trendingMoviesJson = apiRequest("https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1")
    val resultsArray = trendingMoviesJson?.getJSONArray("results") ?: JSONArray()
    return parseMovieList(resultsArray)
}

fun getSimilarMovies(id: Int): List<MovieSearchResult>{
    val trendingMoviesJson = apiRequest("https://api.themoviedb.org/3/movie/${id}/similar?language=en-US&page=1")
    val resultsArray = trendingMoviesJson?.getJSONArray("results") ?: JSONArray()
    return parseMovieList(resultsArray)
}

// Does it pull all this data for each movie in the search result?
// If so, we should only pull the id, title, poster, and popularity for the results.
// Then get all of the extensive data for a movie once the user has selected it
// maybe have a MovieSearchResult class and a MovieDetailed class
fun parseMovieList(movies: JSONArray): MutableList<MovieSearchResult>{
    val movieAttributes: MutableList<MovieSearchResult> = mutableListOf()
    // val excludedKeywordId = 155477 // The ID for "softcore"

    for ( i in 0 until movies.length()){
        val movie = movies.getJSONObject(i)
        val movieToAdd = MovieSearchResult(
            movie.get("id") as Int,
            movie.get("title") as String,
            movie.get("poster_path").toString(),
            movie.get("popularity") as Double // Include popularity
        )

        //If the movie poster is null then it doesn't show up, to change remove the if and leave the "movieAttributes.add(movieToAdd)" as it is
        if(movie.get("poster_path").toString() != "null"){
            movieAttributes.add(movieToAdd)
        }
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


fun getDetailsFromID(id: Int): MovieDetails? {
    val movieJson = apiRequest("https://api.themoviedb.org/3/movie/${id}?language=en-US")
    return movieJson?.let { parseMovieDetails(it) }  // Parse the JSON object here
}

fun getGenreHardCode(): List<Pair<String, Int>> {
    val movieGenre: List<Pair<String, Int>> = listOf(
        "Action" to 28,
        "Adventure" to 12,
        "Animation" to 16,
        "Comedy" to 35,
        "Crime" to 80,
        "Documentary" to 99,
        "Drama" to 18,
        "Family" to 10751,
        "Fantasy" to 14,
        "History" to 36,
        "Horror" to 27,
        "Music" to 10402,
        "Mystery" to 9648,
        "Romance" to 10749,
        "Science Fiction" to 878,
        //"TV Movie" to 10770,
        "Thriller" to 53,
        "War" to 10752,
        "Western" to 37
    )
    return movieGenre
}

//In case we ever need to go back to this
//fun getGenre(): MutableList<Pair<String, Int>> {
//    val genre = apiRequest("https://api.themoviedb.org/3/genre/movie/list?language=en").getJSONArray("genres")
//    val movieGenre: MutableList<Pair<String, Int>> = mutableListOf()
//    for(i in 0 until genre.length()){
//        movieGenre.add(genre.getJSONObject(i).get("name").toString() to (genre.getJSONObject(
//            i
//        ).get("id")) as Int)
//    }
//    return movieGenre
//}

fun displayRandomMovie(movie: Pair<String, Int>): Int? { // Return Int?
    val queryMovie = apiRequest("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en&page=1&sort_by=popularity.desc&with_genres=${movie.second}")

    return if (queryMovie != null) {
        val resultsArray = queryMovie.getJSONArray("results")
        if (resultsArray.length() > 0) {
            val movieId = resultsArray.getJSONObject(Random.nextInt(resultsArray.length())).getInt("id") // Use getInt()
            movieId
        } else {
            Log.e("displayRandomMovie", "No movies found for genre ${movie.first}")
            null // Return null if no movies are found
        }
    } else {
        Log.e("displayRandomMovie", "API request failed")
        null // Return null if API request fails
    }
}
