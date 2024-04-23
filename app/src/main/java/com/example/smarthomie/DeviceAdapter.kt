package com.example.smarthomie

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthomie.databinding.DeviceItemBinding
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.example.smarthomie.databinding.DeviceControllableItemBinding
import com.example.smarthomie.databinding.DeviceControllableItem2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

interface DeviceActionListener {
    fun onPerformQuickAction(device: DeviceDetails)
}

enum class AdapterContext {
    MY_DEVICES, DEVICE_DISCOVERY, MY_DEVICES2
}
/*
* Passing context type to know which activity is accessing Device Adapter
* My devices page needs devices on a recycler view in a different way than device discovery page
* Such as Device Discovery needs only the name of the device, icon, and status and we only need to select those devices to add them to the database
* On the other hand the my devices page needs to fetch the devices from the firestore and put them on the recycler view
* With quick control buttons next to each device and also the ability to click devices and go to their separate page
* so that we could display more info such as device type, mac address potentially, and other relevant info about the device on a separate page
* */
class DeviceAdapter(
    private var devices: MutableList<DeviceDetails> = mutableListOf(),
    private val contextType: AdapterContext,
    private val actionListener: DeviceActionListener? = null,
    private val clickListener: (DeviceDetails) -> Unit,
    private val detailClickListener: ((DeviceDetails) -> Unit)? = null
     // for understanding which page we are working with such as discover devices or my devices
    ) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    fun getDeviceAtPosition(position: Int): DeviceDetails {
        return devices[position]
    }


suspend fun removeDeviceAtPosition(device: DeviceDetails, position: Int) {
    val userDocRef = FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
        FirebaseFirestore.getInstance().collection("Users").document(userId)
    } ?: return

    val deviceDocIdToRemove = device.documentID ?: return

    // Begin a batch write to ensure atomic operations
    val batch = FirebaseFirestore.getInstance().batch()

    // Remove the device from the 'Devices' collection
    val deviceDocRef = FirebaseFirestore.getInstance().collection("Devices").document(deviceDocIdToRemove)
    batch.delete(deviceDocRef)

    // Remove the device's document ID from the user's list of devices
    batch.update(userDocRef, "listOfDevices", FieldValue.arrayRemove(deviceDocIdToRemove))

    // Commit the batch operation
    batch.commit().addOnSuccessListener {
        Log.d("DeviceAdapter", "Device successfully deleted from the user's list and Devices collection")
        devices.removeAt(position)
        notifyItemRemoved(position)
    }.addOnFailureListener { e ->
        Log.w("DeviceAdapter", "Error during batch delete", e)
    }
}

    fun submitList(newDevices: MutableList<DeviceDetails>){
        devices = newDevices
        notifyDataSetChanged()
    }
    inner class DeviceViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(device: DeviceDetails, clickListener: (DeviceDetails) -> Unit) {
        when (binding) {
            is DeviceItemBinding -> { // Binding method for Device Discovery activity
                Log.d("DeviceAdapter", "Binding device with name: ${device.name} and status: ${device.status} and type: ${device.type}")
                binding.deviceName.text = device.name
                binding.deviceStatus.text = device.status
                binding.deviceIcon.setImageResource(when (device.type){

                    "HueBridge" -> R.drawable.ic_hue_bridge
                    "Thermostat" -> R.drawable.ic_thermostat
                    "Smart Light" -> R.drawable.ic_light_bulb
                    "Smart Plug" -> R.drawable.ic_smart_plug
                    else-> R.drawable.ic_generic_device
                })



            }
            is DeviceControllableItemBinding -> { // Binding method for My Devices activity

                Log.d("DeviceAdapter", "Binding device with name: ${device.name} and status: ${device.status}")
                binding.deviceName.text = device.name
                binding.deviceStatus.text = device.status
                binding.deviceIcon.setImageResource(when (device.type){

                    "HueBridge" -> R.drawable.ic_hue_bridge
                    "Thermostat" -> R.drawable.ic_thermostat
                    "Smart Light" -> R.drawable.ic_light_bulb
                    "Smart Plug" -> R.drawable.ic_smart_plug
                    else-> R.drawable.ic_generic_device
                })

                if (device.status == "on"){
                    binding.quickActionButton.text = "ON"
                }
                else{
                    binding.quickActionButton.text = "OFF"
                }

                binding.quickActionButton.setOnClickListener {
                    actionListener?.onPerformQuickAction(device)
                }

                binding.deviceInfoContainer.setOnClickListener {
                    val context = it.context
                    val intent = Intent(context, DeviceDetailsActivity::class.java).apply {
                        putExtra("DEVICE_DETAILS", device)
                    }
                    context.startActivity(intent)
                }

            }
            is DeviceControllableItem2Binding -> {
                binding.deviceName.text = device.name
                binding.deviceStatus.text = device.status
                binding.deviceIcon.setImageResource(
                    when (device.type) {

                        "HueBridge" -> R.drawable.ic_hue_bridge
                        "Thermostat" -> R.drawable.ic_thermostat
                        "Light bulb" -> R.drawable.ic_light_bulb
                        "Smart Plug" -> R.drawable.ic_smart_plug
                        else -> R.drawable.ic_generic_device
                    }
                )
            }

        }

        itemView.setOnClickListener {

            when (contextType) {
                AdapterContext.MY_DEVICES -> {

                }
                AdapterContext.DEVICE_DISCOVERY -> {
                    device.isSelected = !device.isSelected
                    Log.d("DeviceAdapter", "onClick - Device selected state is now ${device.isSelected}")
                    updateBackgroundColor(device.isSelected)
                    clickListener(device)
                    notifyItemChanged(adapterPosition)
                }
                AdapterContext.MY_DEVICES2-> {

                }
            }
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

    private fun performQuickAction(){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = when (contextType){
            AdapterContext.MY_DEVICES -> DeviceControllableItemBinding.inflate(layoutInflater, parent, false)
            AdapterContext.DEVICE_DISCOVERY -> DeviceItemBinding.inflate(layoutInflater, parent, false)
            AdapterContext.MY_DEVICES2 -> DeviceControllableItem2Binding.inflate(layoutInflater, parent, false)
        }
        return DeviceViewHolder(binding);

    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position], clickListener)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = devices.size

}