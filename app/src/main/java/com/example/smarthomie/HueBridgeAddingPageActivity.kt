package com.example.smarthomie

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog

class HueBridgeAddingPageActivity : AppCompatActivity(){
    private lateinit var nsdManager: NsdManager
    private val serviceTypeHue = "_hue._tcp."
    private var isDiscoveryRunning = false
    private var progressDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hue_bridge_adding_page)

        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager

        val searchBridgeButton: Button = findViewById(R.id.NetworkDiscoveryButton)

        searchBridgeButton.setOnClickListener {
            startDiscoveryProcess()
        }
    }

    private fun startDiscoveryProcess() {
        if (isConnectedToWifi()) {
            startDiscovery()
        } else {
            showWifiSettings()// invoke the wifi menu to connect to wifi
        }
    }

    private fun isConnectedToWifi(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun showWifiSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            startActivity(panelIntent)
        } else {
            val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(wifiSettingsIntent)
        }
    }

    private fun startDiscovery() {
        if(!isDiscoveryRunning) {
            nsdManager.discoverServices(serviceTypeHue, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            isDiscoveryRunning = true
            // Start loading dialog
            showLoadingIndicator()
        }
    }

    private fun stopDiscovery() {
        if (isDiscoveryRunning) {
            nsdManager.stopServiceDiscovery(discoveryListener)
            isDiscoveryRunning = false
            //Dismiss loading dialog
            hideLoadingIndicator()
        }
    }

    private  val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
            // In case Discovery Fails

            Log.e("mDNS", "Discovery start failed: Error code:$errorCode")
        }

        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
            // In case Stop fails

            Log.e("mDNS", "Discovery stop failed: Error code:$errorCode")
        }

        override fun onDiscoveryStarted(serviceType: String?) {
            // Discovery started

            Log.d("mDNS", "Service discovery started")
        }

        override fun onDiscoveryStopped(p0: String?) {

            Log.d("mDNS", "Service discovery stopped")
        }

        override fun onServiceFound(service: NsdServiceInfo?) {
            Log.d("mDNS", "Service found")
            if (service?.serviceType == serviceTypeHue) {
                Log.d("mDNS", "Inside If statement of service found")
                runOnUiThread {
                    //Hue bridge found
                    showAddBridgeDialog()
                    stopDiscovery()
                }
            }
        }

        override fun onServiceLost(service: NsdServiceInfo?) {
            // In case service is lost
            Log.d("mDNS", "On service Lost")

        }
    }

    private fun showAddBridgeDialog() {
        Log.d("mDNS", "Inside show add bridge dialog")
        AlertDialog.Builder(this)
            .setTitle("Hue Bridge Found")
            .setMessage("A Hue Bridge has been found. Add it to your smart home setup?")
            .setPositiveButton("Add") { dialog, which ->
                // Handling hue bridge adding
                addUserBridgeToSetup()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override  fun onPause() {
        Log.d("mDNS", "At on Pause start")
        super.onPause()
        stopDiscovery()
        Log.d("mDNS", "At on Pause end")
    }

    override fun onDestroy() {
        Log.d("mDNS", "At on Destroy start")
        super.onDestroy()
        stopDiscovery()
        Log.d("mDNS", "At on Destroy end")
    }

    private fun addUserBridgeToSetup() {
        Log.d("mDNS", "In add User Bridge to User's set up")
    }

    override fun onBackPressed() {
        Log.d("mDNS", "Back button pressed going back to Add/remove device page")
        super.onBackPressed()

    }

    private fun showLoadingIndicator() {
        val builder = AlertDialog.Builder(this)
        // Use a custom layout with a progress bar, or use the default spinner
        builder.setView(R.layout.loading_dialog) // Replace with your custom layout
        builder.setCancelable(false) // Set to false if you don't want the user to cancel it by back button

        progressDialog = builder.create()
        progressDialog?.show()
    }

    private fun hideLoadingIndicator() {
        progressDialog?.dismiss()
    }



}


