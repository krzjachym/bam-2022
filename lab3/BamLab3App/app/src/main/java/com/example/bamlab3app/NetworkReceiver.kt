package com.example.bamlab3app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast


class NetworkReceiver(private val context: Context) : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnected
        val message = if (isConnected == true) "Connected"  else "Not Connected"
        Log.d(MainActivity.TAG, "Is connected: $isConnected")
        Log.d(MainActivity.TAG, "Type: ${networkInfo?.type} ${ConnectivityManager.TYPE_WIFI}")
        Toast.makeText(p0, message, Toast.LENGTH_SHORT).show()
    }
}