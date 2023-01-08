package com.example.newsswipe.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsswipe.R
import com.example.newsswipe.database.DatabaseBookmarks
import com.example.newsswipe.database.DatabaseKeywords
import com.example.newsswipe.models.News
import com.example.newsswipe.ui.adapter.NewsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.koushikdutta.ion.Ion
import com.yuyakaido.android.cardstackview.*
import org.json.JSONArray
import org.json.JSONObject


class NewsActivity : AppCompatActivity(), CardStackListener {

    private var mAuth = FirebaseAuth.getInstance()
    private val databaseKeywords = DatabaseKeywords(this)
    private val databaseBookmarks = DatabaseBookmarks(this)
    private val user = if(mAuth.currentUser != null){mAuth.currentUser?.email.toString()} else{"guest"}
    lateinit var currentNews: News

    private lateinit var articles : MutableList<News>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val settingsButton = findViewById<ImageButton>(R.id.settings_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        val bookmarksButton = findViewById<Button>(R.id.bookmarks_button)
        val username = findViewById<TextView>(R.id.username)

        val cardStackView = findViewById<CardStackView>(R.id.card_stack_view)
        val manager = CardStackLayoutManager(this,this)

        val keywordsList : MutableList<String> = databaseKeywords.findKeywords(user)
        articles = newsAPI(keywordsList)
        //val articles : MutableList<News> = newsAPI(keywordsList)

        Log.i("Settings", articles.toString())
        val mAdapter = NewsAdapter(articles,this)

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

        bookmarksButton.setOnClickListener {
            val intent = Intent(this, BookmarksActivity::class.java)
            startActivity(intent)
        }

    }

    private fun newsAPI(keywordsList: MutableList<String>): MutableList<News> {
        val listNews = mutableListOf<News>()
        if (keywordsList.isEmpty()){
            Log.d("API", "pas de keyword")
            listNews.add(News(getString(R.string.no_keywords),"null","null","null","https://i.postimg.cc/8zJqXQqy/logo.png"))
        }
        else {
            val prefs = getSharedPreferences("Language", Context.MODE_PRIVATE)
            val language = prefs.getString("News_language",null)
            for (word in keywordsList) {
                val request = Ion.with(this)
                    .load("https://newsdata.io/api/1/news?apikey=pub_9348f82517fd2daa328046260e91032902a4&q=$word&language=$language")
                    .setHeader("Accept", "application/json")
                    .setHeader("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .asString()
                    .withResponse()
                    .get()

                val myJSON = JSONObject(request.result.toString())
                //val status = myJSON.getString("status")
                Log.d("API", myJSON.toString())


                val listArticles = myJSON.getString("results")
                Log.d("API", listArticles.length.toString())
                val myJSONArticles = JSONArray(listArticles)

                val nb: Int = if (myJSONArticles.length() > 5) 5
                else myJSONArticles.length()


                Log.d("API", myJSONArticles.length().toString())
                for (i in 0 until nb) {
                    //Log.d("API",i.toString())
                    val row = JSONObject(myJSONArticles.getJSONObject(i).toString())
                    val title = row.getString("title")
                    val author = row.getString("source_id")
                    val url = row.getString("link")
                    var image = row.getString("image_url")
                    val date = row.getString("pubDate")
                    //Log.d("API", "title = $title, author = $author, url = $url, image = $image, date = $date")

                    if (image == "null") image = getString(R.string.link_logo)

                    val n = News(title, author, url, date, image)
                    listNews.add(n)
                    Log.d("API", n.toString())
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
        if (keywordsList.isEmpty()) manager.setCanScrollHorizontal(false) //No swipe
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        //Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        Log.d("CardStackView",direction.toString())

        if (direction.toString() == "Right"){
            databaseBookmarks.addBookmark(user,currentNews.title,currentNews.image,currentNews.url).toInt()
            Toast.makeText(this, getString(R.string.keyword_add_success), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCardRewound() {
        Log.d("CardStackView","card rewound")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView","card canceled")
    }

    override fun onCardAppeared(view: View, position: Int) {
        setupShareButton(position)
        setCurrentNews(position)
    }

    private fun setCurrentNews(position: Int) {
        currentNews = articles[position]
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("CardStackView","card disappear $position")

        if (position == articles.lastIndex){
            setupShareButton(-1)
        }

    }

    private fun setupShareButton(position: Int){
        val shareButton = findViewById<FloatingActionButton>(R.id.share_button)

        if (position != -1 && articles[position].url != "null") {
            shareButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TITLE, articles[position].title)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.share_text).plus(articles[position].url)
                )
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "test"))
            }
        }
        else{
            shareButton.setOnClickListener {
                Toast.makeText(this,getString(R.string.nothing_to_share), Toast.LENGTH_SHORT).show()
            }
        }
    }


}