package com.example.newsswipe.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsswipe.R
import com.example.newsswipe.database.DatabaseBookmarks
import com.example.newsswipe.models.News
import com.example.newsswipe.ui.adapter.BookmarksAdapter

class BookmarksActivity : AppCompatActivity() {

    private val mDatabase = DatabaseBookmarks(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        val backButton = findViewById<Button>(R.id.back_button)

        val bookmarksList = mutableListOf<News>()
        bookmarksList.add(News("title1","author","url","date","image"))

        val recyclerView = findViewById<View>(R.id.bookmarks_recycler_view) as RecyclerView
        val mAdapter = BookmarksAdapter(bookmarksList,mDatabase,this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter

        bookmarksList.add(News("title2","author","url","date","image"))
        bookmarksList.add(News("title3","author","url","date","image"))

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                val deletedCourse: News = bookmarksList[viewHolder.adapterPosition]

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                bookmarksList.removeAt(viewHolder.adapterPosition)

                // below line is to notify our item is removed from adapter.
                mAdapter.notifyItemRemoved(viewHolder.adapterPosition)

            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recyclerView)


        backButton.setOnClickListener{
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

    }
}