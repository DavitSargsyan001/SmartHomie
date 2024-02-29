package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class DeviceDiscoveryActivity : AppCompatActivity() {
    // Need to discover devices on the network by using the Hue Bridge
    // Get the IP of the Bridge for sending requests to the API (Got it from previous activity to not call firestore again)
    // Also need the hue username that we get in the Hue bridge discovery process (Again got it from previous activity)
    // Both things can be taken from either the local or from the Firestore Firebase database
    // After both things are done
    // -> When the Discover Devices button is pressed
    // -> Discover hue brand devices on the network with the use of the API
    // -> Display the Discovered devices on a scroll view or any other appropriate form
    // -> Let the User select the devices to be added to his devices list in the My Devices Page
    // -> I guess we are also going to need another button on the page for saving/adding those devices
    // Once that is done user is free to navigate to other pages
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_discovery_page)
        //val intent = Intent(this, AddRemoveActivity::class.java)
        val hueBridgeIp = intent.getStringExtra("IP_ADDRESS")
        val hueBridgeUsername = intent.getStringExtra("USERNAME")
        val SaveButton: Button = findViewById(R.id.add_devices)
        val DiscoverDevicesButton: Button = findViewById(R.id.discover_button)
        val homeButton: ImageButton = findViewById(R.id.ibHome2)

        homeButton.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }

        DiscoverDevicesButton.setOnClickListener {
            DiscoverDevices(hueBridgeIp, hueBridgeUsername);
        }

    }

    private fun DiscoverDevices(hueIP: String?, hueUsername: String?){
        Log.d("DeviceDiscoveryActivity", "Got IP: $hueIP")
        Log.d("DeviceDiscoveryActivity", "Got Username: $hueUsername")
        val url = "http://$hueIP/api/$hueUsername/lights"

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
                //Failure
                Log.d("DeviceDiscoveryActivity", "Failed to send request")
            }

            override fun onResponse(call: Call, response: Response) {
                TODO("Not yet implemented")
            }
        })
    }
}