package com.example.newsswipe.ui.adapter

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
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class BookmarksAdapter(private val list: MutableList<News>, private val context: Context) : RecyclerView.Adapter<BookmarksAdapter.ViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.news_layout_bookmark, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position].title


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
            .load(list[position].image)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.error)
            .into(holder.image)

        holder.card.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(list[position].url))
            context.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var title = view.findViewById(R.id.title) as TextView
        var image = view.findViewById(R.id.image) as ImageView
        var card = view.findViewById(R.id.card) as CardView

        override fun onClick(v: View?) {

        }
    }
}

