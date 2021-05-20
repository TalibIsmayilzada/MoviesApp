package com.talib.moviesapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talib.moviesapp.data.model.Movie
import com.talib.moviesapp.domain.MoviesRepository
import com.talib.moviesapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MoviesRepository) : ViewModel() {

    sealed class Event {
        class Success(val result: List<Movie>) : Event()
        class Failure(val errorText: String) : Event()
        object Loading : Event()
    }

    private val trendingMoviesChannel = Channel<Event>()
    val trendingMoviesFlow = trendingMoviesChannel.receiveAsFlow()

    fun getTrendingMovies() = viewModelScope.launch(Dispatchers.IO) {
        trendingMoviesChannel.send(Event.Loading)

        when (val response = repository.getTrendingMovies()) {
            is Resource.Success -> {
                response.data?.let {
                    trendingMoviesChannel.send(Event.Success(response.data))
                } ?: kotlin.run {
                    trendingMoviesChannel.send(Event.Failure(response.message ?: ""))
                }
            }
            is Resource.Error -> {
                trendingMoviesChannel.send(Event.Failure(response.message ?: ""))
            }
        }
    }
}