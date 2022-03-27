package com.example.bamlab3app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BluetoothReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Toast.makeText(p0, "BT state change", Toast.LENGTH_SHORT).show()
        Log.d("BT Receiver", "onReceive: bluetooth state changed")
    }

}