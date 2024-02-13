package com.example.smarthomie

class DeviceAdapter(private val devices: List<DeviceDetails>) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(device: DeviceDetails) {
            itemView.deviceName.text = device.name
            itemView.deviceStatus.text = device.status
            // Set the device icon based on the device type or status
            val iconRes = when (device.type) {
                "HueBridge" -> R.drawable.ic_hue_bridge
                "Thermostat" -> R.drawable.ic_thermostat
                // Add more cases as needed
                else -> R.drawable.ic_generic_device // A generic icon for unknown types
            }
            itemView.deviceIcon.setImageResource(iconRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int = devices.size
}
