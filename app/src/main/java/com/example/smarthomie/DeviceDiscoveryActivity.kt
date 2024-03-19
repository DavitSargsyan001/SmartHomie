package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException

class DeviceDiscoveryActivity : AppCompatActivity() {
    private lateinit var adapter: DeviceAdapter
    var devices = mutableListOf<DeviceDetails>()
    // Need to discover devices on the network by using the Hue Bridge
    // Get the IP of the Bridge for sending requests to the API (Got it from previous activity to not call firestore again)
    // Also need the hue username that we get in the Hue bridge discovery process (Again got it from previous activity)
    // Both things can be taken from either the local or from the Firestore Firebase database
    // After both things are done
    // -> When the Discover Devices button is pressed
    // -> Discover hue brand devices on the network with the use of the API
    // -> Display the Discovered devices on a scroll view or any other appropriate form
    // -> Let the User select the devices to be added to his devices list in the My Devices Page
    // -> initially save button is disabled, once the user selects a device to add they can click the save button to save them to their devices and control later on from my devices page
    // -> I guess we are also going to need another button on the page for saving/adding those devices
    // Once that is done user is free to navigate to other pages
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_discovery_page)
        //val intent = Intent(this, AddRemoveActivity::class.java)
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val hueBridgeIp = intent.getStringExtra("IP_ADDRESS")
        val hueBridgeUsername = intent.getStringExtra("USERNAME")
        val saveButton: Button = findViewById(R.id.add_devices)
        val DiscoverDevicesButton: Button = findViewById(R.id.discover_button)
        val homeButton: ImageButton = findViewById(R.id.ibHome2)
        saveButton.isEnabled = false

        adapter = DeviceAdapter(devices) {device ->
            // Here we handle the selection change.
            // Toggle the selected state.
            //-device.isSelected = !device.isSelected
            // If you have a save or add button, you could enable/disable it here based on the number of selected items.
            val selectedCount = devices.count { it.isSelected }
            Log.d("DeviceDiscoveryActivity", "coutn of selected devices:  $selectedCount")
            // Enable the button if at least one device is selected.
            saveButton.isEnabled = selectedCount > 0
            // You might need to refresh the RecyclerView to update the visual state.
            Log.d("DeviceDiscoveryActivity", "Index of device:  ${devices.indexOf(device)}")
            adapter.notifyItemChanged(devices.indexOf(device))

        }
        val recyclerView: RecyclerView = findViewById(R.id.devicesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        homeButton.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }

        DiscoverDevicesButton.setOnClickListener {
            DiscoverDevices(hueBridgeIp, hueBridgeUsername, userId);
        }

        saveButton.setOnClickListener {
            val selectedDevices = devices.filter {it.isSelected}

            if (selectedDevices.isNotEmpty()) {
                // Add selected devices to the user's list of devices in the database
                addSelectedDevicesToUserList(selectedDevices, hueBridgeIp)

                // Optional: Provide feedback or navigate
                //Toast.makeText(this, "Devices added successfully", Toast.LENGTH_SHORT).show()
                //val intent = Intent(this, MyDevicesActivity::class.java)
                //startActivity(intent)
            } else {
                // No devices selected, provide feedback
                Toast.makeText(this, "No devices selected", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addSelectedDevicesToUserList(selectedDevices: List<DeviceDetails>, hueIP: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Example: Assuming you have a collection of users, each with a subcollection for devices
        selectedDevices.forEach { device ->
            // Prepare the device data
            val deviceData = hashMapOf(
                "IP" to hueIP,
                "Name: " to device.name,
                "Status: " to device.status,
                "Type: " to device.type,
                "hueBridgeUsername: " to device.hueBridgeUsername,
                "ownerUserID" to  userId,
                "isSelected" to false
            )

            db.collection("Devices").add(deviceData)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Device added with ID: ${documentReference.id}")
                    Toast.makeText(this, "Device added to the database", Toast.LENGTH_SHORT).show()

                    val deviceId = documentReference.id
                    saveDeviceIdOnUsersListOfDevices(deviceId, userId)

                }
        }
    }

    private fun saveDeviceIdOnUsersListOfDevices(deviceId: String, userId: String){
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("Users").document(userId)

        userDocRef.update("listOfDevices", FieldValue.arrayUnion(deviceId))
            .addOnSuccessListener {
                Log.d("Firestore", "Device ID added to user's listOfDevices succesfully")
            }
            .addOnFailureListener {e->
                Log.w("Firestore", "Error adding device ID to user's listOfDevices",e )
            }
    }

    private fun DiscoverDevices(hueIP: String?, hueUsername: String?, ownerUsername: String?){
        Log.d("DeviceDiscoveryActivity", "Got IP: $hueIP")
        Log.d("DeviceDiscoveryActivity", "Got Username: $hueUsername")
        val url = "http://$hueIP/api/$hueUsername/lights"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Failure
                Log.d("DeviceDiscoveryActivity", "Failed to send request")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res -> // Ensure the response body is closed after its use
                    val responseBody = res.body?.string()
                    if (res.isSuccessful && responseBody != null) {
                        try {
                            val discoveredDevices = mutableListOf<DeviceDetails>()
                            val jsonObject = JSONObject(responseBody)
                            jsonObject.keys().forEach { key ->
                                val lightObject = jsonObject.getJSONObject(key)
                                val state = lightObject.getJSONObject("state")
                                val isOn = state.getBoolean("on")
                                val name = lightObject.getString("name")

                                val deviceDetails = DeviceDetails(
                                    deviceId = key,
                                    name = name,
                                    status = if (isOn) "On" else "Off",
                                    type = if (name == "Hue smart plug") "Smart Plug" else "Smart Light",
                                    ip = hueIP ?: "",
                                    hueBridgeUsername = hueUsername ?: "",
                                    ownerUserID = ownerUsername ?: "",
                                    isSelected = false
                                )
                                discoveredDevices.add(deviceDetails)

                                Log.d("DeviceDiscoveryActivity", "Device ID: $key, Name: $name, Is On: $isOn")
                            }

                            runOnUiThread{
                                devices.clear()
                                devices.addAll(discoveredDevices)
                                adapter.submitList(devices.toList())
                            }

                        } catch (e: JSONException) {
                            Log.e("DeviceDiscoveryActivity", "Could not parse JSON", e)
                        }
                    } else {
                        Log.e("DeviceDiscoveryActivity", "Response not successful")
                    }
                }
            }
        })
    }



}