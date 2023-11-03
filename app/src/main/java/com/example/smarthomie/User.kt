package com.example.smarthomie

class User {
    val id : Int = 0
    val email: String = ""
    val passwordHash: String = ""
    //var preferences: UserPreferences?

    // Assuming listOfDevices holds device IDs or references to Device objects.
    var listOfDevices: MutableList<Device> = mutableListOf()

    fun addDevice(device: Device) {
        // Check if the device is not already added
        if (device !in listOfDevices) {
            listOfDevices.add(device)
            // Optionally, if Device has an owner property: device.owner = this
        }
    }

    fun removeDevice(device: Device) {
        listOfDevices.remove(device)
        // If Device has an owner property: device.owner = null
    }






}

//Register
//login
//addDevice
//removeDevice