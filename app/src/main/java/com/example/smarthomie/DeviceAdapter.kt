package com.example.smarthomie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthomie.databinding.DeviceItemBinding
import android.util.Log
import android.widget.ExpandableListView.OnChildClickListener
import androidx.core.content.ContextCompat



class DeviceAdapter(
    private var devices: List<DeviceDetails> = listOf(),
    private val clickListener: (DeviceDetails) -> Unit
    ) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    fun submitList(newDevices: List<DeviceDetails>){
        devices = newDevices
        notifyDataSetChanged()
    }
    inner class DeviceViewHolder(private val binding: DeviceItemBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(device: DeviceDetails, clickListener: (DeviceDetails) -> Unit) {
        binding.deviceName.text = device.name
        binding.deviceStatus.text = device.status
        Log.d("DeviceAdapter", "Device type is ${device.type}")
        binding.deviceIcon.setImageResource(when (device.type){

            "Hue Bridge" -> R.drawable.ic_hue_bridge
            "Thermostat" -> R.drawable.ic_thermostat
            "Light bulb" -> R.drawable.ic_light_bulb
            "Smart Plug" -> R.drawable.ic_smart_plug
            else-> R.drawable.ic_generic_device
        })

        if (device.isSelected) {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, androidx.browser.R.color.browser_actions_bg_grey))
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, androidx.constraintlayout.widget.R.color.button_material_light))
        }

        itemView.setOnClickListener {
            device.isSelected = !device.isSelected
            //updateBackgroundColor(device.isSelected)
            clickListener(device)
            notifyItemChanged(adapterPosition, Any())
        }
    }
        private fun updateBackgroundColor(isSelected: Boolean) {
            if (isSelected) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, androidx.browser.R.color.browser_actions_bg_grey))
                Log.d("DeviceAdapter", "Item is selected, changing color.")
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, androidx.constraintlayout.widget.R.color.button_material_light))
                Log.d("DeviceAdapter", "Item is unselected, changing color.")
            }
        }
}



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder{
        val binding = DeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding);
    }
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position], clickListener)
    }
    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // Payload is present, update just the views that need changing
            holder.bind(devices[position], clickListener)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = devices.size

}