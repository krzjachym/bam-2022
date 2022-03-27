package com.example.bamlab3app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val requestButton by lazy { findViewById<Button>(R.id.requestBtn) }
    private val networkStatusButton by lazy { findViewById<Button>(R.id.networkStatusBtn) }
    private val showContactsButton by lazy { findViewById<Button>(R.id.showContactsBtn) }
    private val networkReceiver by lazy { NetworkReceiver(this) }

    companion object {
        val TAG = MainActivity::class.java.canonicalName
        var READ_CONTACTS_PERMISSION_REQUEST_CODE = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestButton.setOnClickListener { onRequestButton() }
        networkStatusButton.setOnClickListener { onNetworkStatusButton() }
        showContactsButton.setOnClickListener { onShowContactsButton() }

        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onRequestButton() {

        val url = URL("https://jsonplaceholder.typicode.com/posts")
        GlobalScope.launch {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET" // optional default is GET
                inputStream.bufferedReader().use {
                    it.forEachLine { line -> Log.d("ACT", line) }
                    //it.lines().forEach { line -> Log.d("ACT", line)

                }
            }
        }
    }

    private fun onNetworkStatusButton() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo;
        val isConnected = networkInfo?.isConnected
        val message = if (isConnected == true) "Connected" else "Not Connected"
        Log.d(TAG, "Is connected: $isConnected")
        Log.d(TAG, "Type: ${networkInfo?.type} ${ConnectivityManager.TYPE_WIFI}")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onShowContactsButton() {
        Log.d(TAG, "onShowContactsButton: clicked the showcontack cklik")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_PERMISSION_REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "onRequestPermissionsResult: granted")
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )
                while (cursor!!.moveToNext()) {
                    val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    Log.d(TAG, "Contact ${contactId} ${displayName}")
                }
            } else {
                Log.d(TAG, "onRequestPermissionsResult: not granted")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }
}