package com.example.newsswipe.models

data class News(
    var title: String,
    var author: String,
    var url: String,
    var date: String,
    var image: String) {


    override fun toString(): String {
        return "News(title='$title', author='$author', url='$url', date='$date', image='$image')"
    }


}