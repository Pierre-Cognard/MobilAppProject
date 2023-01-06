package com.example.newsswipe.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsswipe.R
import com.example.newsswipe.database.DatabaseBookmarks
import com.example.newsswipe.database.DatabaseKeywords
import com.example.newsswipe.models.News
import com.google.firebase.auth.FirebaseAuth

class BookmarksAdapter(private val list: MutableList<News>, private val database: DatabaseBookmarks, private val context: Context) : RecyclerView.Adapter<BookmarksAdapter.ViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()
    private val user = if(mAuth.currentUser != null){mAuth.currentUser?.email.toString()} else{"guest"}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.news_layout_bookmark, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {



        override fun onClick(v: View?) {

        }
    }
}

