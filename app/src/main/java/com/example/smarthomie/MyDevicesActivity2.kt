package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarthomie.databinding.MyDevices2Binding
import androidx.lifecycle.Observer


class MyDevicesActivity2 : AppCompatActivity(), DeviceActionListener  {
    private lateinit var binding: MyDevices2Binding
    private lateinit var adapter: DeviceAdapter
    private lateinit var deviceControlService: DeviceControlService
    private val viewModel: DeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyDevices2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val cancelButton: Button = findViewById(R.id.button10)

        cancelButton.setOnClickListener{
            val intent = Intent(this, MyGroup::class.java)
            startActivity(intent)
        }
        deviceControlService = DeviceControlService()
        setupRecyclerView()
        observeDevices()
    }

    private fun setupRecyclerView() {

        val clickListener: (DeviceDetails) -> Unit = {device ->
            Toast.makeText(this, "Clicked on device: ${device.name}", Toast.LENGTH_SHORT).show()


        }

        adapter = DeviceAdapter(mutableListOf(),  AdapterContext.MY_DEVICES2, this, clickListener)
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.devicesRecyclerView.adapter = adapter
    }

    private fun observeDevices(){
        viewModel.devicesLiveData.observe(this, Observer{ devices ->
            // Update your RecyclerView adapter with the list of devices
            adapter.submitList(devices.toMutableList())
            //(binding.devicesRecyclerView.adapter as DeviceAdapter).submitList(devices)
        })
    }

    override fun onPerformQuickAction(device: DeviceDetails) {
    }
}