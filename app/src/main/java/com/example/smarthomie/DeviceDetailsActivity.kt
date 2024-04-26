package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        findViewById<TextView>(R.id.tvDeviceIP).text = device.ip


        findViewById<ImageButton>(R.id.ibHome3).setOnClickListener {
            startActivity(Intent(this, homePage::class.java))
        }



        if (device.type == "Smart Light" || device.type == "Smart Plug") {

            if (device.type == "Smart Light"){
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

            }


            val switchOnOff = findViewById<Switch>(R.id.switchControlDevice)

            val button = findViewById<Button>(R.id.timerButton)
            button.setOnClickListener {
                showScheduleDialog()
            }

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

    private fun showScheduleDialog(){
        val dialogView = layoutInflater.inflate(R.layout.time_picking_page, null)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)

        val dialog = dialogBuilder.create()

        val timePickerOn = dialogView.findViewById<TimePicker>(R.id.timePicker_on)
        val timePickerOff = dialogView.findViewById<TimePicker>(R.id.timePicker_off)
        val buttonSetSchedule = dialogView.findViewById<Button>(R.id.button_set_schedule)
        val buttonCancel = dialogView.findViewById<Button>(R.id.button_set_schedule_cancel)

        buttonSetSchedule.setOnClickListener {
            // Logic to handle scheduling
            val onTime = getTimeFromPicker(timePickerOn)
            val offTime = getTimeFromPicker(timePickerOff)
            // Apply scheduling logic
            Log.d("Schedule", "Schedule set: ON at $onTime, OFF at $offTime")
            dialog.dismiss()  // Dismiss the dialog
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()  // Dismiss the dialog without doing anything
        }

        dialog.show()  // Show the dialog
    }

    private fun getTimeFromPicker(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        return String.format("%02d:%02d", hour, minute)
    }


}
