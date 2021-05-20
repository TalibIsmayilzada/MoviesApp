package com.talib.moviesapp.domain

import com.talib.moviesapp.data.model.Movie
import com.talib.moviesapp.util.Resource

interface MoviesRepository {

    suspend fun getTrendingMovies(): Resource<List<Movie>>
}