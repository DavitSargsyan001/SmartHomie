package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.example.smarthomie.databinding.MyDevicesBinding
import com.example.smarthomie.DeviceViewModel
import com.google.firebase.auth.FirebaseAuth

class MyDevicesActivity : AppCompatActivity() {
    private lateinit var binding: MyDevicesBinding
    private val viewModel: DeviceViewModel by viewModels {
        //DeviceViewModel(DatabaseBuilder.getInstance(application).deviceDetailsDao())
        val deviceDao = DatabaseBuilder.getInstance(application).deviceDetailsDao()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")
        DeviceViewModelFactory(deviceDao, userId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeButton: ImageButton = findViewById(R.id.ibHome3)

        homeButton.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        observeDevices()
    }

    private fun setupRecyclerView() {

        val clickListener: (DeviceDetails) -> Unit = {device ->
            // Example action: Show a toast with the device name
            Toast.makeText(this, "Clicked on device: ${device.name}", Toast.LENGTH_SHORT).show()

            // Here, you could also start an activity to show device details or perform other actions.
            // Example:
            // val intent = Intent(this, DeviceDetailActivity::class.java)
            // intent.putExtra("DEVICE_ID", device.deviceId)
            // startActivity(intent)
        }
        val adapter = DeviceAdapter(listOf(), clickListener)
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.devicesRecyclerView.adapter = adapter
    }

    private fun observeDevices() {
        viewModel.devices.observe(this, { devices ->
            (binding.devicesRecyclerView.adapter as DeviceAdapter).submitList(devices)
        })
    }

}