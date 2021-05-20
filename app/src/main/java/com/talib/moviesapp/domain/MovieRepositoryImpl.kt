package com.talib.moviesapp.domain

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.talib.moviesapp.data.api.API
import com.talib.moviesapp.data.db.MovieDao
import com.talib.moviesapp.data.model.Movie
import com.talib.moviesapp.util.Resource


class MovieRepositoryImpl(
    private val activity: Activity,
    private val dao: MovieDao,
    private val api: API
) : MoviesRepository {


    override suspend fun getTrendingMovies(): Resource<List<Movie>> {

        if (isNetworkAvailable(activity)) {
            Log.d("fasfdasdas","dasdasd")
            val response = api.getTrendingMovies("c2c1864db4f6009a6b4dcbef7c2ad2d7")
            val result = response.body()
            result?.let {
                if (response.isSuccessful) {
                    it.results?.let { it1 -> dao.insertMovies(it1) }
                    return Resource.Success(result.results!!)
                } else {
                    return Resource.Error(response.message())
                }
            }
        } else {
            Log.d("fasfdasdas","dasdasd2")
            val result = dao.getAllMovies()
            Log.d("fasfdasdas",result.toString())

            return Resource.Success(result)
        }






        return Resource.Error("unknown error occured")

    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->    true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->   true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->   true
                else ->     false
            }
        }
        // For below 29 api
        else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }
}