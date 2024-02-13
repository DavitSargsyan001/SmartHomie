package com.example.smarthomie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MyDevicesActivity : AppCompatActivity() {

    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var devicesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_devices)

        devicesRecyclerView = findViewById(R.id.devicesRecyclerView)
        devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchDevicesAndDisplay()
    }

    private fun fetchDevicesAndDisplay() {
        lifecycleScope.launch {
            val db = DatabaseBuilder.getInstance(applicationContext)
            val devices = db.deviceDetailsDao().getAllDevices() // Ensure this method exists in your DAO
            deviceAdapter = DeviceAdapter(devices)
            devicesRecyclerView.adapter = deviceAdapter
        }
    }
}
