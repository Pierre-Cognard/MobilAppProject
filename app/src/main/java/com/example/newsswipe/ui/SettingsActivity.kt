package com.example.newsswipe.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import java.util.Locale


class SettingsActivity : AppCompatActivity() {

    private val mDatabase = SqliteDatabase(this)
    private val mAuth = FirebaseAuth.getInstance()
    private val user = if(mAuth.currentUser != null){mAuth.currentUser?.email.toString()} else{"guest"}

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val listLanguage = arrayOf("English","French","Spanish")

        val prefs = getSharedPreferences("Language", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val list : MutableList<String> = mDatabase.listKeywords()

        val appLanguageButton = findViewById<ImageView>(R.id.app_language_button)
        val appLanguageText = findViewById<TextView>(R.id.current_app_language)

        val newsLanguageButton = findViewById<ImageView>(R.id.news_language_button)
        val newsLanguageText = findViewById<TextView>(R.id.current_news_language)

        val addKeywordButton = findViewById<Button>(R.id.add_keyword_button)
        val backButton = findViewById<Button>(R.id.back_button)
        val keyword = findViewById<TextView>(R.id.keyword)

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

        var appChecked = 0
        when(prefs.getString("App_language",null)){
            "en" -> {
                appLanguageText.text = listLanguage[0]
                appChecked = 0
            }
            "fr" -> {
                appLanguageText.text = listLanguage[1]
                appChecked = 1
            }
            "es" -> {
                appLanguageText.text = listLanguage[2]
                appChecked = 2
            }
        }

        appLanguageButton.setOnClickListener {
            Log.i("Settings", "App Language")
            val mBuilder = AlertDialog.Builder(this)
            mBuilder.setTitle("Choose App Language")
            //val checked =
            mBuilder.setSingleChoiceItems(R.array.languages, appChecked) { dialog, which ->
                if (which == 0) {
                    setLocal("en")
                } else if (which == 1) {
                    setLocal("fr")
                } else if (which == 2) {
                    setLocal("es")
                }
                dialog.dismiss()
            }
            val mDialog = mBuilder.create()
            mDialog.show()
        }

        when(prefs.getString("News_language",null)){
            "en" -> newsLanguageText.text = listLanguage[0]
            "fr" -> newsLanguageText.text = listLanguage[1]
            "es" -> newsLanguageText.text = listLanguage[2]
        }

        newsLanguageButton.setOnClickListener {

            var newsChecked = 0
            when(prefs.getString("News_language",null)){
                "en" -> newsChecked = 0
                "fr" -> newsChecked = 1
                "es" -> newsChecked = 2
            }
            Log.i("Settings", "News Language")
            val mBuilder = AlertDialog.Builder(this)
            mBuilder.setTitle("Choose News Language")
            mBuilder.setSingleChoiceItems(listLanguage, newsChecked) { dialog, which ->
                if (which == 0) {
                    editor.putString("News_language","en").apply()
                    newsLanguageText.text = listLanguage[0]
                } else if (which == 1) {
                    editor.putString("News_language","fr").apply()
                    newsLanguageText.text = listLanguage[1]
                } else if (which == 2) {
                    editor.putString("News_language","es").apply()
                    newsLanguageText.text = listLanguage[2]
                }
                dialog.dismiss()
            }
            val mDialog = mBuilder.create()
            mDialog.show()
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

    private fun setLocal(language: String){ //function to change application language
        Log.i("test", language)
        val local = Locale(language)
        Locale.setDefault(local)
        val res = this.resources
        val conf = Configuration()
        conf.setLocale(local)
        res.updateConfiguration(conf,res.displayMetrics)
        Log.i("test", "REFRESH")

        val prefs = getSharedPreferences("Language", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("App_language",language).apply()

        val refresh = Intent(this,SettingsActivity::class.java)
        startActivity(refresh)
    }

}