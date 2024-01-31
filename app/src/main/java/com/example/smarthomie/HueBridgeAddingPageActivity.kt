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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        if (isDiscoveryRunning && ::nsdManager.isInitialized) {
            nsdManager.stopServiceDiscovery(discoveryListener)
            isDiscoveryRunning = false
            //Dismiss loading dialog
            hideLoadingIndicator()
        }

        progressDialog?.dismiss()
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

         fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
            Log.d("mDNS", "Resolve Succeeded. $serviceInfo")
            if(serviceInfo != null) {
                val hostAddress = serviceInfo.host.hostAddress
                Log.d("mDNS", "Hue Bridge IP Address: $hostAddress")
                saveBridgeDetailsToDatabase(hostAddress)
            }
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
        builder.setCancelable(true) // Set to false if you don't want the user to cancel it by back button

        builder.setNegativeButton("Cancel") { dialog, which ->
            stopDiscovery()
            dialog.dismiss()
            Toast.makeText(this, "Discovery canceled", Toast.LENGTH_SHORT).show()
        }

        progressDialog = builder.create()
        progressDialog?.show()
    }

    private fun hideLoadingIndicator() {
        progressDialog?.dismiss()
    }

    private fun saveBridgeDetailsToDatabase(ipAddress: String) {
        // Logic to save the IP address and any other details to your database
        // For example, if using Firebase Firestore:
        /*
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val db = FirebaseFirestore.getInstance()
            val bridgeDetails = hashMapOf("ipAddress" to ipAddress)
            db.collection("users").document(it).update("hueBridge", bridgeDetails)
                .addOnSuccessListener { Log.d("Database", "Hue Bridge details saved successfully") }
                .addOnFailureListener { e -> Log.w("Database", "Error saving Hue Bridge details", e) }
        }
        */
        /*
        * How to add the bridge to database correctly
        * 1. Get the bridge IP but don't store the IP in the database or maybe we could store it but not depend on it?
        * 2. Set the device Type to Hue bridge
        * 3. Set the Name property to Hue bridge
        * 4. Set status to appropriate status such as Connected
        * 5. set the ownerUserID to whatever the current user's ID is
        * 6. Add the Document ID generated for the device to User's list of Devices
        * */
        Toast.makeText(this, "Saving bridge details to the database", Toast.LENGTH_SHORT).show()
    }


}


