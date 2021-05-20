package com.talib.moviesapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.talib.moviesapp.R
import com.talib.moviesapp.data.api.API
import com.talib.moviesapp.data.db.MovieDatabase
import com.talib.moviesapp.domain.MovieRepositoryImpl
import com.talib.moviesapp.domain.MoviesRepository
import com.talib.moviesapp.ui.home.adapters.TrendingAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.collect
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var moviesRepository: MoviesRepository

    private lateinit var myAdapter: TrendingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)


        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()


        val api: API = retrofit.create(API::class.java)

        val dao = MovieDatabase.getDatabase(requireActivity()).movieDao()
        moviesRepository = MovieRepositoryImpl(requireActivity(),dao, api)
        homeViewModelFactory = HomeViewModelFactory(repository = moviesRepository)
        homeViewModel = ViewModelProviders.of(requireActivity(), homeViewModelFactory)
            .get(HomeViewModel::class.java)
        trendingListRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myAdapter = TrendingAdapter()
        trendingListRV.adapter = myAdapter

        lifecycleScope.launchWhenCreated {
            homeViewModel.getTrendingMovies()
            homeViewModel.trendingMoviesFlow.collect { event ->

                when (event) {
                    is HomeViewModel.Event.Loading -> {
                        loading.visibility = View.VISIBLE
                    }
                    is HomeViewModel.Event.Failure -> {
                        loading.visibility = View.GONE
                        Toast.makeText(requireContext(), event.errorText, Toast.LENGTH_SHORT).show()
                    }
                    is HomeViewModel.Event.Success -> {
                        loading.visibility = View.GONE
                        myAdapter.differ.submitList(event.result)
                    }
                }

            }
        }
    }


}