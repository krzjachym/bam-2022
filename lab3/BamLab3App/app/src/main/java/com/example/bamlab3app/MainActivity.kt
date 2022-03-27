package com.example.bamlab3app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private val requestButton by lazy { findViewById<Button>(R.id.requestBtn)}
    private val networkStatusButton by lazy { findViewById<Button>(R.id.networkStatusBtn)}
    private val showContactsButton by lazy {findViewById<Button>(R.id.showContactsBtn)}
    private val networkReceiver by lazy {NetworkReceiver(this)}
    private val bluetoothReceiver by lazy {BluetoothReceiver()}

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
        registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    private fun onRequestButton() {
        val requestThread = Thread(RequestRunnable())
        requestThread.start()
    }

    private fun onNetworkStatusButton(){
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo;
        val isConnected = networkInfo?.isConnected
        val message = if (isConnected == true) "Connected"  else "Not Connected"
        Log.d(TAG, "Is connected: $isConnected")
        Log.d(TAG, "Type: ${networkInfo?.type} ${ConnectivityManager.TYPE_WIFI}")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onShowContactsButton(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_PERMISSION_REQUEST_CODE
        )
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
    }

    override fun onDestroy()
    {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }
}