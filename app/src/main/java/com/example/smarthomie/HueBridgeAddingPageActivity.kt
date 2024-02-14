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
import org.json.JSONObject
import okhttp3.OkHttpClient
import okhttp3.*
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class HueBridgeAddingPageActivity : AppCompatActivity(){
    private lateinit var nsdManager: NsdManager
    private val serviceTypeHue = "_hue._tcp."
    private var isDiscoveryRunning = false
    private var progressDialog: AlertDialog? = null
    private var hostIP: String? = null

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

                hostIP = hostAddress
            }
        }
    }

    private fun showAddBridgeDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hue Bridge Found")
            .setMessage("A Hue Bridge has been found. Add it to your smart home setup?")
            .setPositiveButton("Add") { dialog, which ->
                AlertDialog.Builder(this)
                    .setTitle("Instructions")
                    .setMessage("Press the circular button on the Hue Bridge and wait a couple of minutes to obtain necessary information")
                    .setPositiveButton("I Pressed the button") { dialog, which ->
                        // Here you pass the callback implementation to handle the response
                        sendPostMessageToHueAPI(object : HueBridgeCallback {
                            override fun onSuccess(username: String) {
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "Bridge added successfully.", Toast.LENGTH_LONG).show()
                                    saveBridgeDetailsToFirestore(username)

                                }
                            }

                            override fun onFailure(error: String) {
                                runOnUiThread {
                                    AlertDialog.Builder(this@HueBridgeAddingPageActivity)
                                        .setTitle("Error")
                                        .setMessage("Failed to add the Hue Bridge: $error. Would you like to retry?")
                                        .setPositiveButton("Retry") { dialog, which ->
                                            showAddBridgeDialog() // Retry by showing the dialog again
                                        }
                                        .setNegativeButton("Cancel", null)
                                        .show()
                                }
                            }
                        })
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

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

    private fun saveBridgeDetailsToFirestore(hueUserName: String) {
        Toast.makeText(this, "Saving bridge details to the database", Toast.LENGTH_SHORT).show()
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val bridgeIp = hostIP ?: return

        val bridgeDetails = hashMapOf(
            "IP" to bridgeIp,
            "Name: " to "HueBridge",
            "Status: " to "Initializing",
            "Type: " to "HueBridge",
            "hueBridgeUsername: " to hueUserName,
            "ownerUserID" to userId
        )

        db.collection("Devices").add(bridgeDetails)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Device added with ID: ${documentReference.id}")
                Toast.makeText(this, "Device added to the database", Toast.LENGTH_SHORT).show()

                val deviceId = documentReference.id // Firestore document ID as Device ID
                SaveBridgeDetailsToLocalDatabase(deviceId, bridgeIp, hueUserName, userId)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding device", e)
                Toast.makeText(this, "Device not added to the database due to errors!", Toast.LENGTH_SHORT).show()
            }



    }

    fun sendPostMessageToHueAPI(callback: HueBridgeCallback) {
        val url = "http://$hostIP/api"
        val jsonBody = JSONObject().apply {
            put("devicetype", "com.example.smarthomie#app")
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure, e.g., log the error or show an error message to the user
                // Remember to perform any UI updates on the main thread
                callback.onFailure(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    val responseBody = res.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonArray = JSONArray(responseBody)
                            if (jsonArray.length() > 0) {
                                val responseObject = jsonArray.getJSONObject(0)
                                if (responseObject.has("success")) {
                                    val successObject = responseObject.getJSONObject("success")
                                    val username = successObject.getString("username")
                                    callback.onSuccess(username)
                                    // Use the username for future requests
                                    // Here, you might want to update the UI or store the username,
                                    // remember to do so on the UI thread if affecting the UI
                                } else if (responseObject.has("error")) {
                                    val errorObject = responseObject.getJSONObject("error")
                                    val errorDescription = errorObject.getString("description")
                                    callback.onFailure(errorDescription)
                                    // Handle the error, inform the user
                                    // Remember to update the UI on the main thread if necessary
                                }
                            }
                        } catch (e: JSONException) {
                            // Handle JSON parsing error
                        }
                    }
                }
            }
        })
    }

    interface HueBridgeCallback {
        fun onSuccess(username: String)
        fun onFailure(error: String)
    }

    fun SaveBridgeDetailsToLocalDatabase(deviceID: String, bridgeIP: String, HueBridgeUsername: String, OwnerUserID: String){
        val deviceDetails = DeviceDetails(
            deviceId = deviceID, // Generate or obtain a unique ID
            name = "Hue Bridge", // Example name
            type = "Hue Bridge",
            status = "Connected",
            ip = bridgeIP,
            hueBridgeUsername = HueBridgeUsername,// The username obtained from Hue API
            ownerUserID = OwnerUserID
        )

        lifecycleScope.launch {
            val db = DatabaseBuilder.getInstance(applicationContext)
            db.deviceDetailsDao().insert(deviceDetails)
        }
    }





}


