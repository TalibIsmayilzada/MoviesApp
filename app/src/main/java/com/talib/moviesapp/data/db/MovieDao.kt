package com.talib.moviesapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.talib.moviesapp.data.model.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(galleryModel: List<Movie>)

    @Query("SELECT * FROM movie_table")
    fun getAllMovies(): List<Movie>

}