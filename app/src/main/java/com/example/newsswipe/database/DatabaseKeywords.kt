package com.example.newsswipe.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseKeywords(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            val createKeywordsTable = "CREATE TABLE $TABLE_KEYWORDS($COLUMN_ID INTEGER PRIMARY KEY,$COLUMN_KEYWORD TEXT,$COLUMN_USER TEXT)"
            db.execSQL(createKeywordsTable)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_KEYWORDS")
            onCreate(db)
        }

        fun listKeywords(): MutableList<String> {
            val sql = "select * from $TABLE_KEYWORDS"
            val db = this.readableDatabase
            val storeKeywords = arrayListOf<String>()
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToFirst()) {
                do {
                    val keyword = cursor.getString(0)
                    val user = cursor.getString(2)
                    storeKeywords.add(keyword)
                    storeKeywords.add(user)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return storeKeywords
        }

        fun addKeyword(keyword: String, user: String): Long {
            val values = ContentValues()
            values.put(COLUMN_KEYWORD, keyword)
            values.put(COLUMN_USER, user)
            val db = this.writableDatabase
            return db.insert(TABLE_KEYWORDS, null, values)
        }

        fun deleteKeyword(keyword: String,user: String): Int {
            val db = this.writableDatabase
            return db.delete(TABLE_KEYWORDS, "$COLUMN_KEYWORD = '$keyword' AND $COLUMN_USER = '$user'",null)
        }

        companion object {
            private const val DATABASE_VERSION = 5
            private const val DATABASE_NAME = "usersKeywords"
            private const val TABLE_KEYWORDS = "keywords"

            private const val COLUMN_ID = "_id"
            private const val COLUMN_KEYWORD = "keyword"
            private const val COLUMN_USER = "user"
        }

        fun findKeywords(user: String): MutableList<String> {
            val sql = "select * from $TABLE_KEYWORDS where user = '$user'"
            val db = this.readableDatabase
            val storeKeywords = arrayListOf<String>()
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToFirst()) {
                do {
                    val keyword = cursor.getString(1)
                    storeKeywords.add(keyword)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return storeKeywords
        }
    }
