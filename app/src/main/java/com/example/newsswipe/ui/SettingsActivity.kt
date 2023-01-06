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

        val prefs = getSharedPreferences("Language", Context.MODE_PRIVATE)
        val listLanguage: Array<String> =  resources.getStringArray(R.array.languages)

        val list : MutableList<String> = mDatabase.listKeywords()

        val appLanguageButton = findViewById<ImageView>(R.id.app_language_button)
        val appLanguageText = findViewById<TextView>(R.id.current_app_language)

        val newsLanguageButton = findViewById<ImageView>(R.id.news_language_button)
        val newsLanguageText = findViewById<TextView>(R.id.current_news_language)

        val addKeywordButton = findViewById<Button>(R.id.add_keyword_button)
        val backButton = findViewById<Button>(R.id.back_button)
        val keywordInput = findViewById<TextView>(R.id.keyword)

        val recyclerView = findViewById<View>(R.id.keyword_recycler_view) as RecyclerView
        val mAdapter = KeywordAdapter(list,mDatabase,this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter

        addKeywordButton.setOnClickListener {
            if (keywordInput.text.toString() == "") Toast.makeText(this, getString(R.string.error_empty_keyword), Toast.LENGTH_SHORT).show() // test if the TextView is empty
            else if (list.contains(keywordInput.text.toString())) Toast.makeText(this, getString(R.string.error_keyword_already_exist), Toast.LENGTH_SHORT).show() // test if the keyword already exists
            else mAdapter.addKeyword(keywordInput.text.toString())
            keywordInput.text = ""
            hideKeyboard()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        var appChecked = 0
        when(prefs.getString("App_language",null)){
            "en" -> appChecked = 0
            "fr" -> appChecked = 1
            "es" -> appChecked = 2
        }
        appLanguageText.text = listLanguage[appChecked]

        appLanguageButton.setOnClickListener {
            Log.i("Settings", "App Language")
            val mBuilder = AlertDialog.Builder(this)
            mBuilder.setTitle("Choose App Language")
            //val checked =
            mBuilder.setSingleChoiceItems(R.array.languages, appChecked) { dialog, language ->
                when(language){
                    0 -> setAppLanguage("en")
                    1 -> setAppLanguage("fr")
                    2 -> setAppLanguage("es")
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
            mBuilder.setSingleChoiceItems(listLanguage, newsChecked) { dialog, language ->
                when(language){
                    0 -> setNewsLanguage("en",0)
                    1 -> setNewsLanguage("fr",1)
                    2 -> setNewsLanguage("es",2)
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

    private fun setAppLanguage(language: String){ //function to change application language
        val prefs = getSharedPreferences("Language", Context.MODE_PRIVATE)
        prefs.edit().putString("App_language",language).apply()

        val local = Locale(language)
        Locale.setDefault(local)
        val res = resources
        val conf = Configuration()
        conf.setLocale(local)
        res.updateConfiguration(conf,res.displayMetrics)
        startActivity(Intent(this,SettingsActivity::class.java))
    }

    private fun setNewsLanguage(language: String, position: Int){
        val prefs = getSharedPreferences("Language", Context.MODE_PRIVATE)
        prefs.edit().putString("News_language",language).apply()

        val listLanguage: Array<String> =  resources.getStringArray(R.array.languages)
        val newsLanguageText = findViewById<TextView>(R.id.current_news_language)
        newsLanguageText.text = listLanguage[position]
    }

}