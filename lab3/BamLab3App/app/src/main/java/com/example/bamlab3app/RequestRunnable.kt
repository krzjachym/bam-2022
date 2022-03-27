package com.example.bamlab3app

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.net.HttpURLConnection
import java.net.URL

class RequestRunnable: Runnable {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun run() {
        val url = URL("https://jsonplaceholder.typicode.com/posts")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET" // optional default is GET
            inputStream.bufferedReader().use {
                it.lines().forEach { line -> Log.d("ACT", line)
                }
            }
        }
    }
}