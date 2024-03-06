package com.example.smarthomie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthomie.databinding.DeviceItemBinding
import android.util.Log

class DeviceAdapter(private var devices: List<DeviceDetails> = listOf()) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    fun submitList(newDevices: List<DeviceDetails>){
        devices = newDevices
        notifyDataSetChanged()
    }

class DeviceViewHolder(val binding: DeviceItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(device: DeviceDetails) {
        binding.deviceName.text = device.name
        binding.deviceStatus.text = device.status
        Log.d("DeviceAdapter", "Device type is ${device.type}")
        binding.deviceIcon.setImageResource(when (device.type){

            "Hue Bridge" -> R.drawable.ic_hue_bridge
            "Thermostat" -> R.drawable.ic_thermostat
            "Light bulb" -> R.drawable.ic_light_bulb
            "Smart plug" -> R.drawable.ic_smart_plug
            else-> R.drawable.ic_generic_device
        })
        /*
        itemView.setOnClickListener {
            onClick(device)
        }
         */
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