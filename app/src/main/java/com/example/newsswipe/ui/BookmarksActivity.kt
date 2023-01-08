package com.example.newsswipe.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsswipe.R
import com.example.newsswipe.database.DatabaseBookmarks
import com.example.newsswipe.models.News
import com.example.newsswipe.ui.adapter.BookmarksAdapter
import com.google.firebase.auth.FirebaseAuth

class BookmarksActivity : AppCompatActivity() {

    private val databaseBookmarks = DatabaseBookmarks(this)
    private var mAuth = FirebaseAuth.getInstance()
    private val user = if(mAuth.currentUser != null){mAuth.currentUser?.email.toString()} else{"guest"}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        val backButton = findViewById<Button>(R.id.back_button)

        val bookmarksList = databaseBookmarks.findBookmarks(user)
        Log.i("bookmarks", bookmarksList.toString())

        val recyclerView = findViewById<View>(R.id.bookmarks_recycler_view) as RecyclerView
        val mAdapter = BookmarksAdapter(bookmarksList,this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter


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


                val check = databaseBookmarks.deleteBookmark(deletedCourse.url,user)
                if (check == 1) Toast.makeText(applicationContext, getString(R.string.keyword_delete_success), Toast.LENGTH_SHORT).show()
                else Toast.makeText(applicationContext, getString(R.string.keyword_delete_error), Toast.LENGTH_SHORT).show()

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                bookmarksList.removeAt(viewHolder.adapterPosition)

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