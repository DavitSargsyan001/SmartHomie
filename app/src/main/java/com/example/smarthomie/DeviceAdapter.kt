package com.example.smarthomie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthomie.databinding.DeviceItemBinding

class DeviceAdapter(private var devices: List<DeviceDetails> = listOf()) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    fun submitList(newDevices: List<DeviceDetails>){
        devices = newDevices
        notifyDataSetChanged()
    }

class DeviceViewHolder(val binding: DeviceItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(device: DeviceDetails) {
        binding.deviceName.text = device.name
        binding.deviceStatus.text = device.status


        binding.deviceIcon.setImageResource(when (device.type){
            "HueBridge" -> R.drawable.ic_hue_bridge
            "Thermostat" -> R.drawable.ic_thermostat
            "Lightbulb" -> R.drawable.ic_light_bulb
            "Smartplug" -> R.drawable.ic_smart_plug
            else-> R.drawable.ic_generic_device
        })
    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder{
        val binding = DeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding);
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = devices.size

}