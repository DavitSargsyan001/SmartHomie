package com.example.smarthomie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.example.smarthomie.databinding.MyDevicesBinding
import com.example.smarthomie.DeviceViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smarthomie.DeviceControlService


class MyDevicesActivity : AppCompatActivity(), DeviceActionListener {
    private lateinit var binding: MyDevicesBinding
    private lateinit var adapter: DeviceAdapter
    private lateinit var deviceControlService: DeviceControlService
    private val viewModel: DeviceViewModel by viewModels ()
    /*
    {
        //DeviceViewModel(DatabaseBuilder.getInstance(application).deviceDetailsDao())
        val deviceDao = DatabaseBuilder.getInstance(application).deviceDetailsDao()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")
        DeviceViewModelFactory(deviceDao, userId)
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeButton: ImageButton = findViewById(R.id.ibHome3)

        homeButton.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }
        deviceControlService = DeviceControlService()
        setupRecyclerView()
        observeDevices()
        //viewModel.fetchDevicesFromFirestore()
    }

    private fun setupRecyclerView() {

        val clickListener: (DeviceDetails) -> Unit = {device ->
            Toast.makeText(this, "Clicked on device: ${device.name}", Toast.LENGTH_SHORT).show()


        }

        adapter = DeviceAdapter(listOf(),  AdapterContext.MY_DEVICES, this, clickListener)
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.devicesRecyclerView.adapter = adapter
    }

    private fun observeDevices(){
        viewModel.devicesLiveData.observe(this, Observer{ devices ->
        // Update your RecyclerView adapter with the list of devices
        adapter.submitList(devices)
        //(binding.devicesRecyclerView.adapter as DeviceAdapter).submitList(devices)
    })
    }

    override fun onPerformQuickAction(device: DeviceDetails) {
        /*
        Toast.makeText(this, "Quick action for ${device.name}", Toast.LENGTH_SHORT).show()
        val hueBridgeIP = device.ip
        val hueUsername = device.hueBridgeUsername
        Log.d("MyDevicesActivity", "IP: ${device.ip}  Hue Username: ${device.hueBridgeUsername}  Device Status: ${device.status}")
        val url = "http://$hueBridgeIP/api/$hueUsername/lights"
*/
        //{
            val isOn = device.status == "On" // Determine the current state based on your status representation
            deviceControlService.toggleDeviceOnOff(device.deviceId!!, !isOn, device.ip!!, device.hueBridgeUsername!!) { success ->
                runOnUiThread {
                    if (success) {
                        // Update device status in UI accordingly
                        Toast.makeText(this, "Device toggled successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to toggle device", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
