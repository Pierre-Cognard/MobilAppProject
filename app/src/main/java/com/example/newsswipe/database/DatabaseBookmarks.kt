package com.example.newsswipe.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.newsswipe.models.News

class DatabaseBookmarks(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            val createKeywordsTable = "CREATE TABLE $TABLE_BOOKMARKS($COLUMN_ID INTEGER PRIMARY KEY,$COLUMN_USER TEXT,$COLUMN_NEWS_TITLE TEXT,$COLUMN_NEWS_IMAGE TEXT, $COLUMN_NEWS_URL TEXT)"
            db.execSQL(createKeywordsTable)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKMARKS")
            onCreate(db)
        }


        fun addBookmark(user: String, title: String, image: String, url: String): Long {
            val values = ContentValues()
            values.put(COLUMN_USER, user)
            values.put(COLUMN_NEWS_TITLE, title)
            values.put(COLUMN_NEWS_IMAGE, image)
            values.put(COLUMN_NEWS_URL, url)
            val db = this.writableDatabase
            return db.insert(TABLE_BOOKMARKS, null, values)
        }

        fun deleteBookmark(url: String,user: String): Int {
            val db = this.writableDatabase
            return db.delete(TABLE_BOOKMARKS, "$COLUMN_NEWS_URL = '$url' AND $COLUMN_USER = '$user'",null)
        }

        companion object {
            private const val DATABASE_VERSION = 5
            private const val DATABASE_NAME = "usersBookmarks"
            private const val TABLE_BOOKMARKS = "bookmarks"

            private const val COLUMN_ID = "_id"
            private const val COLUMN_USER = "user"
            private const val COLUMN_NEWS_TITLE = "title"
            private const val COLUMN_NEWS_IMAGE = "image"
            private const val COLUMN_NEWS_URL = "url"
        }

        fun findBookmarks(user: String): MutableList<News> {
            val sql = "select * from $TABLE_BOOKMARKS where user = '$user'"
            val db = this.readableDatabase
            val storeBookmarks = arrayListOf<News>()
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToFirst()) {
                do {
                    val title = cursor.getString(2)
                    val image = cursor.getString(3)
                    val url = cursor.getString(4)
                    storeBookmarks.add(News(title,"null",url,"null",image))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return storeBookmarks
        }
    }
