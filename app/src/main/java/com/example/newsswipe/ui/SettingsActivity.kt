package com.example.newsswipe.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsswipe.R
import com.example.newsswipe.database.SqliteDatabase
import com.example.newsswipe.ui.adapter.KeywordAdapter
import com.google.firebase.auth.FirebaseAuth


class SettingsActivity : AppCompatActivity() {

    //private val list = mutableListOf<String>()
    private val mDatabase = SqliteDatabase(this)
    private val mAuth = FirebaseAuth.getInstance()

    private val user = if(mAuth.currentUser != null){mAuth.currentUser?.email.toString()} else{"guest"}

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("Language", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val list : MutableList<String> = mDatabase.listKeywords()
        val appLanguageButton = findViewById<Button>(R.id.app_language_button)

        val addKeywordButton = findViewById<Button>(R.id.add_keyword_button)
        val backButton = findViewById<Button>(R.id.back_button)
        val keyword = findViewById<TextView>(R.id.keyword)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val listLanguage = arrayOf("English","French","Spanish")
        val languageAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,listLanguage)
        spinner.adapter = languageAdapter

        when(prefs.getString("News_language",null)){
            "en" -> spinner.setSelection(0)
            "fr" -> spinner.setSelection(1)
            "es" -> spinner.setSelection(2)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(listLanguage[position]){
                    "English" -> editor.putString("News_language","en").apply()
                    "French" -> editor.putString("News_language","fr").apply()
                    "Spanish" -> editor.putString("News_language","es").apply()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i("Settings", "no selection")
            }
        }

        val recyclerView = findViewById<View>(R.id.keyword_recycler_view) as RecyclerView
        val mAdapter = KeywordAdapter(list,mDatabase,this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter


        addKeywordButton.setOnClickListener {
            if (keyword.text.toString() == "") Toast.makeText(this, getString(R.string.error_empty_keyword), Toast.LENGTH_SHORT).show() // test if the TextView is empty
            else if (list.contains(keyword.text.toString())) Toast.makeText(this, getString(R.string.error_keyword_already_exist), Toast.LENGTH_SHORT).show() // test if the keyword already exists
            else mAdapter.addKeyword(keyword.text.toString())
            keyword.text = ""
            hideKeyboard()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        appLanguageButton.setOnClickListener {
            Log.i("Settings", "App Language")
        }

        list.clear()
        for (elem in mDatabase.findKeywords(user)) list.add(0,elem)
        mAdapter.notifyDataSetChanged()

    }

    private fun hideKeyboard() { //function to hide the keyboard
        val view: View? = this.currentFocus
        if (view != null) {
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}