package com.example.newsswipe.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsswipe.R
import com.example.newsswipe.models.News
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val newsList: MutableList<News>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.news_item, parent, false))
    }

    @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.title.text = newsList[position].title

        if (newsList[position].author != "null") holder.author.text = newsList[position].author

        if (newsList[position].date != "null") {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(newsList[position].date)
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val dateStr = date?.let { formatter.format(it) }
            holder.date.text = dateStr.toString()
        }

        if (newsList[position].title != "null") {
            holder.progressBar.visibility = VISIBLE

            Picasso
                .get()
                .load(newsList[position].image)
                .placeholder( R.drawable.progress_animation )
                .into(holder.image)

            holder.progressBar.visibility = INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var title = view.findViewById(R.id.title) as TextView
        var author = view.findViewById(R.id.author) as TextView
        var date = view.findViewById(R.id.date) as TextView
        var image = view.findViewById(R.id.image) as ImageView
        var progressBar = view.findViewById(R.id.progressBar) as ProgressBar

        override fun onClick(v: View?) {

        }
    }
}

