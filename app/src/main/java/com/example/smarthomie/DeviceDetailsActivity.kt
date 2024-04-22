package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthomie.DeviceControlService

class DeviceDetailsActivity : AppCompatActivity() {
    private lateinit var deviceControlService: DeviceControlService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val device = intent.getParcelableExtra<DeviceDetails>("DEVICE_DETAILS")
        deviceControlService = DeviceControlService()

        val layoutRes = when (device?.type) {
            "HueBridge" -> R.layout.activity_bridge_detail
            "Smart Light" -> R.layout.activity_light_detail
            "Smart Plug" -> R.layout.activity_plug_detail
            else -> R.layout.activity_device_detail  // A default or error layout
        }

        setContentView(layoutRes)

        if (device != null) {
            setupUI(device)
        }
    }

    private fun setupUI(device: DeviceDetails) {
        findViewById<TextView>(R.id.tvDeviceName).text = device.name
        findViewById<TextView>(R.id.tvDeviceStatus).text = device.status
        findViewById<TextView>(R.id.tvDeviceType).text = device.type
        findViewById<TextView>(R.id.tvDeviceIDnumeric).text = device.deviceId
        findViewById<TextView>(R.id.tvDeviceIP).text = device.ip
        findViewById<TextView>(R.id.tvDeviceUniqueID).text = device.id

        findViewById<ImageButton>(R.id.ibHome3).setOnClickListener {
            startActivity(Intent(this, homePage::class.java))
        }



        if (device.type == "Smart Light") {
            val brightnessControl = findViewById<SeekBar>(R.id.seekBar)
            brightnessControl.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    deviceControlService.setBrightness(device.deviceId!!, progress, device.ip!!, device.hueBridgeUsername!!) { success ->
                        runOnUiThread {
                            if (success) {
                                Toast.makeText(this@DeviceDetailsActivity, "Brightness adjusted", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@DeviceDetailsActivity, "Failed to adjust brightness", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }



                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Optional: Implement if needed
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Optional: Implement if needed
                }
            })

            val switchOnOff = findViewById<Switch>(R.id.switchControlDevice)

            switchOnOff.isChecked = device.status == "On"

            switchOnOff.setOnCheckedChangeListener {_, isChecked ->
                val newStatus = if (isChecked) "On" else "Off"
                deviceControlService.toggleDeviceOnOff(device.deviceId!!, isChecked, device.ip!!, device.hueBridgeUsername!!) {success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Device turned $newStatus", Toast.LENGTH_SHORT).show()
                            device.status = newStatus
                        } else {
                            Toast.makeText(this, "Failed to turn $newStatus", Toast.LENGTH_SHORT).show()
                            switchOnOff.isChecked = !isChecked
                        }
                    }
                }
            }
        }
    }
}
