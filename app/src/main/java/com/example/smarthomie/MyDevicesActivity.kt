package com.example.smarthomie

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.example.smarthomie.databinding.MyDevicesBinding
import com.example.smarthomie.DeviceViewModel

class MyDevicesActivity : AppCompatActivity() {
    private lateinit var binding: MyDevicesBinding
    private val viewModel: DeviceViewModel by viewModels {
        //DeviceViewModel(DatabaseBuilder.getInstance(application).deviceDetailsDao())
        val deviceDao = DatabaseBuilder.getInstance(application).deviceDetailsDao()
        DeviceViewModelFactory(deviceDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeDevices()
    }

    private fun setupRecyclerView() {
        val adapter = DeviceAdapter()
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.devicesRecyclerView.adapter = adapter
    }

    private fun observeDevices() {
        viewModel.devices.observe(this, { devices ->
            (binding.devicesRecyclerView.adapter as DeviceAdapter).submitList(devices)
        })
    }
}
