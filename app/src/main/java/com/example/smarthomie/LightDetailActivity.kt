package com.example.smarthomie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LightDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light_detail)

        val deviceId = intent.getStringExtra("DEVICE_ID")

        loadDeviceDetails(deviceId)
        // Setup UI components like sliders and color pickers
    }

    private fun loadDeviceDetails(deviceId: String?) {
        // Fetch details from your database or a ViewModel and update the UI
    }
}