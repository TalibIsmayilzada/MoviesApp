package com.talib.moviesapp.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.talib.moviesapp.R
import com.talib.moviesapp.data.model.Movie

class TrendingAdapter : RecyclerView.Adapter<TrendingAdapter.MovieViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImage: ImageView = itemView.findViewById(R.id.movieImage)
        val title: TextView = itemView.findViewById(R.id.movieTitle)
        val releaseDate: TextView = itemView.findViewById(R.id.movieReleaseDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.trending_recycler_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.thumbnailImage.load(
            "https://image.tmdb.org/t/p/original${
                differ.currentList.getOrNull(
                    position
                )?.poster_path
            }"
        )
        holder.title.text = differ.currentList.getOrNull(position)?.title
        holder.releaseDate.text = differ.currentList.getOrNull(position)?.release_date
    }

    override fun getItemCount(): Int = differ.currentList.size
}