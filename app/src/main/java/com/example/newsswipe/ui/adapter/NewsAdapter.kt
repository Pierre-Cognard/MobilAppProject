package com.example.newsswipe.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.newsswipe.R
import com.example.newsswipe.models.News
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val newsList: MutableList<News>, private val context: Context) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.news_item, parent, false))
    }

    @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (newsList[position].url != "null") {
            holder.card.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsList[position].url))
                context.startActivity(browserIntent)
            }
        }

        holder.title.text = newsList[position].title

        if (newsList[position].author != "null") holder.author.text = newsList[position].author

        if (newsList[position].date != "null") {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(newsList[position].date)
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val dateStr = date?.let { formatter.format(it) }
            holder.date.text = dateStr.toString()
        }


        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.apply {
            strokeWidth = 10f
            centerRadius = 100f
            setColorSchemeColors(
                context.getColor(R.color.blue_button_dark)
            )
            start()
        }
        Picasso
            .get()
            .load(newsList[position].image)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.error)
            .into(holder.image)




    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var title = view.findViewById(R.id.title) as TextView
        var author = view.findViewById(R.id.author) as TextView
        var date = view.findViewById(R.id.date) as TextView
        var image = view.findViewById(R.id.image) as ImageView
        var card = view.findViewById(R.id.card) as CardView

        override fun onClick(v: View?) {

        }
    }
}

