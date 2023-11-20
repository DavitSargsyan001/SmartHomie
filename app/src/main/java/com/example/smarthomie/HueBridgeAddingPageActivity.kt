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

class HueBridgeAddingPageActivity : AppCompatActivity() {

    //test
    private val serviceTypeHue = "_hue._tcp."

    // nsdManager is a system service that helps with network service discovery.
    private lateinit var nsdManager: NsdManager

    private val discoveryTimeoutMillis: Long = 30000 // 30 seconds to search

    private var discoveryDialog: AlertDialog? = null
    private var discoveryHandler: Handler? = null

    // discoveryListener is responsible for handling discovery events like service found or lost.
    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.e("mDNS", "Discovery start failed: Error code:$errorCode")
        }

        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.e("mDNS", "Discovery stop failed: Error code:$errorCode")
        }

        override fun onDiscoveryStarted(serviceType: String?) {
            Log.d("mDNS", "Service discovery started")
        }

        override fun onDiscoveryStopped(serviceType: String?) {
            Log.i("mDNS", "Service discovery stopped")
        }

        override fun onServiceFound(service: NsdServiceInfo?) {
            Log.d("mDNS", "Service discovery success: $service")
            if (service?.serviceType == serviceTypeHue) {
                Log.d("mDNS", "Found Hue Bridge service")
                // If a service matching the Hue Bridge type is found, try to resolve it to get more info.
                nsdManager.resolveService(service, createResolveListener())
                runOnUiThread {
                    discoveryDialog?.dismiss()
                }
            }
        }

        override fun onServiceLost(service: NsdServiceInfo?) {
            Log.e("mDNS", "Service lost: $service")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hue_bridge_adding_page)

        // Get the nsdManager from the system services.
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager

        // Get a reference to the button and set an onClickListener on it.
        val searchBridgeButton: Button = findViewById(R.id.NetworkDiscoveryButton)
        searchBridgeButton.setOnClickListener {
            // When the button is clicked, check for a Wi-Fi connection.
            if (isConnectedToWifi()) {
                // If the device is connected to Wi-Fi, start the discovery process.
                showLoadingDialog()
                startDiscovery()
            } else {
                // If the device is not connected to Wi-Fi, show Wi-Fi settings for the user to connect.
                showWifiSettings()
            }
        }
    }

    // This function checks if the device is currently connected to a Wi-Fi network.
    private fun isConnectedToWifi(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    // This function starts the service discovery process using the nsdManager.
    private fun startDiscovery() {
        Log.d("mDNS", "Inside startDiscovery()")
        nsdManager.discoverServices(serviceTypeHue, NsdManager.PROTOCOL_DNS_SD, discoveryListener)

        Handler(Looper.getMainLooper()).postDelayed({
            nsdManager.stopServiceDiscovery(discoveryListener)
            //Notify the user that the discovery process has timed out
            Toast.makeText(this, "Discovery timed out", Toast.LENGTH_LONG).show()
        }, discoveryTimeoutMillis)
    }

    // This function creates and returns an NsdManager.ResolveListener to handle resolving a service.
    private fun createResolveListener(): NsdManager.ResolveListener {
        return object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.e("mDNS", "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                Log.d("mDNS", "Resolve Succeeded. $serviceInfo")
                if (serviceInfo != null) {
                    Log.d("mDNS", "Got the IP. $serviceInfo")
                    val hostAddress = serviceInfo.host.hostAddress
                    // The Hue Bridge's IP address can be used here to connect to the bridge.
                }
            }
        }
    }

    // This function shows the Wi-Fi settings screen so the user can connect to Wi-Fi.
    private fun showWifiSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            startActivity(panelIntent)
        } else {
            val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(wifiSettingsIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // When the activity is destroyed, stop the service discovery to clean up resources.
        if (::nsdManager.isInitialized) {
            nsdManager.stopServiceDiscovery(discoveryListener)
        }
        discoveryDialog?.dismiss()
    }

    private fun showLoadingDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(false)

        builder.setNegativeButton("Cancel"){ dialog, which ->
            stopDiscovery()
            Toast.makeText(this, "Discovery canceled", Toast.LENGTH_SHORT).show()
        }

        discoveryDialog = builder.create()
        discoveryDialog?.show()

        discoveryHandler = Handler(Looper.getMainLooper()).apply {
            postDelayed({
                if(discoveryDialog?.isShowing == true) {
                    stopDiscovery()
                    discoveryDialog?.dismiss()
                    Toast.makeText(this@HueBridgeAddingPageActivity, "Discovery timed out", Toast.LENGTH_SHORT).show()
                }
            }, 30000)
        }
    }

    private fun stopDiscovery() {
        discoveryHandler?.removeCallbacksAndMessages(null)
        nsdManager.stopServiceDiscovery(discoveryListener)
        discoveryDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        stopDiscovery()
    }
}
