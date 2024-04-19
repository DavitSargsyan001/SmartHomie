package com.example.smarthomie

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeviceDetailsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)

        // Retrieve device details passed from MyDevicesActivity
        val deviceName = intent.getStringExtra("DEVICE_NAME")
        val deviceStatus = intent.getStringExtra("DEVICE_STATUS")
        val deviceType = intent.getStringExtra("DEVICE_TYPE")
        val deviceId = intent.getStringExtra("DEVICE_ID")
        // ... You can pass more details as needed

        // Set the device details to TextViews
        findViewById<TextView>(R.id.tvDeviceName).text = deviceName
        findViewById<TextView>(R.id.tvDeviceStatus).text = "Status: $deviceStatus"
        findViewById<TextView>(R.id.tvDeviceType).text = "Type: $deviceType"

        // Set up the SeekBar listener to control brightness
        val seekBarBrightness = findViewById<SeekBar>(R.id.seekBarBrightness)
        seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Handle brightness control here
                controlDeviceBrightness(deviceId, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun controlDeviceBrightness(deviceId: String?, brightness: Int) {
        // Send brightness change to your device control service
        // You might want to scale the brightness value as per your device's requirements
    }
}