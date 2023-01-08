package com.example.newsswipe.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.newsswipe.R
import com.example.newsswipe.database.DatabaseKeywords
import com.google.firebase.auth.FirebaseAuth

class KeywordAdapter(private val keywordList: MutableList<String>, private val database: DatabaseKeywords, private val context: Context) : RecyclerView.Adapter<KeywordAdapter.ViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()
    private val user = if(mAuth.currentUser != null){mAuth.currentUser?.email.toString()} else{"guest"}

    fun addKeyword(keyword: String) {
        val check = database.addKeyword(keyword, user).toInt() // add keyword to database
        if (check == -1) Toast.makeText(context, context.getString(R.string.keyword_add_error), Toast.LENGTH_SHORT).show()
        else{
            Toast.makeText(context, context.getString(R.string.keyword_add_success), Toast.LENGTH_SHORT).show()
            keywordList.clear()
            for (elem in database.findKeywords(user)) keywordList.add(0,elem)
            notifyItemInserted(0)
        }
    }

    private fun deleteKeyword(keyword: String, position: Int) {
        val check = database.deleteKeyword(keyword,user)
        if (check == 1){
            Toast.makeText(context, context.getString(R.string.keyword_delete_success), Toast.LENGTH_SHORT).show() // delete key word from database
            keywordList.remove(keyword) // delete keyword from list
            notifyItemRemoved(position) // notify adapter
        }
        else Toast.makeText(context, context.getString(R.string.keyword_delete_error), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.keyword_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keyword = keywordList[position]
        holder.keyword.text = keyword
        holder.deleteButton.setOnClickListener{ // binding delete button
            deleteKeyword(keyword,keywordList.indexOf(keyword))
        }
    }

    override fun getItemCount(): Int {
        return keywordList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var keyword = itemView.findViewById<View>(R.id.keyword_text) as TextView
        var deleteButton = itemView.findViewById<View>(R.id.delete) as ImageView
        override fun onClick(v: View?) {
        }
    }
}

