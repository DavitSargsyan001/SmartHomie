package com.example.smarthomie

class DeviceAdapter(private val devices: List<DeviceDetails>) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(device: DeviceDetails) {
            itemView.deviceName.text = device.name
            itemView.deviceStatus.text = device.status
            // Set the device icon based on the device type or status
            itemView.deviceIcon.setImageResource(R.drawable.ic_device) // Adjust as needed
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
