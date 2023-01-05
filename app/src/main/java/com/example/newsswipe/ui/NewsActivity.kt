package com.example.newsswipe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.newsswipe.R
import com.example.newsswipe.database.SqliteDatabase
import com.example.newsswipe.models.News
import com.example.newsswipe.ui.adapter.NewsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.koushikdutta.ion.Ion
import com.yuyakaido.android.cardstackview.*
import org.json.JSONArray
import org.json.JSONObject


class NewsActivity : AppCompatActivity(), CardStackListener {

    private var mAuth = FirebaseAuth.getInstance()
    private val mDatabase = SqliteDatabase(this)
    private val user = if(mAuth.currentUser != null){mAuth.currentUser?.email.toString()} else{"guest"}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val settingsButton = findViewById<Button>(R.id.settings_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        val username = findViewById<TextView>(R.id.username)

        val cardStackView = findViewById<CardStackView>(R.id.card_stack_view)
        val manager = CardStackLayoutManager(this,this)

        val keywordsList : MutableList<String> = mDatabase.findKeywords(user)
        val articles : MutableList<News> = newsAPI(keywordsList)

        val mAdapter = NewsAdapter(articles)

        cardStackView.layoutManager = manager
        cardStackView.adapter = mAdapter

        init(manager,keywordsList)

        if (mAuth.currentUser != null) username.text = mAuth.currentUser?.email
        else{
            logoutButton.text = getString(R.string.login)
            username.text = getString(R.string.guest)
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            //Log.i("Settings", "settings")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun newsAPI(keywordsList: MutableList<String>): MutableList<News> {
        val listNews = mutableListOf<News>()

        if (keywordsList.isEmpty()){
            Log.d("API", "pas de keyword")
            listNews.add(News("You need to add keywords in settings","null","null","null","https://i.postimg.cc/8zJqXQqy/logo.png"))
        }
        else {
            for (word in keywordsList) {
                val request = Ion.with(this)
                    .load("https://newsapi.org/v2/everything?apiKey=7d127856d20d4cfd830aca5f42dfa305&pageSize=5&q=$word")
                    .setHeader("Accept", "application/json")
                    .setHeader("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .asString()
                    .withResponse()
                    .get()

                val myJSON = JSONObject(request.result.toString())
                val status = myJSON.getString("status")
                Log.d("API", "status = $status")

                val listArticles = myJSON.getString("articles")

                val myJSONArticles = JSONArray(listArticles)

                for (i in 0 until myJSONArticles.length()) {
                    //Log.d("API",i.toString())
                    val row = JSONObject(myJSONArticles.getJSONObject(i).toString())
                    val title = row.getString("title")
                    val author = row.getString("author")
                    val url = row.getString("url")
                    val image = row.getString("urlToImage")
                    val date = row.getString("publishedAt")
                    //Log.d("API", "title = $title, author = $author, url = $url, image = $image, date = $date")
                    val n = News(title, author, url, date, image)
                    listNews.add(n)
                }
            }
        }
        return listNews
    }


    private fun init(manager: CardStackLayoutManager,keywordsList: MutableList<String>) {
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.4f)
        manager.setMaxDegree(30.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setStackFrom(StackFrom.Top)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())

        if (keywordsList.isEmpty()) manager.setCanScrollHorizontal(false)

    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        Log.d("CardStackView","card swiped")
    }

    override fun onCardRewound() {
        Log.d("CardStackView","card rewound")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView","card canceled")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(com.yuyakaido.android.cardstackview.R.id.title)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("CardStackView","card disappear")
    }
}