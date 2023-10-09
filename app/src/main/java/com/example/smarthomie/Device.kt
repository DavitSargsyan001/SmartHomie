package com.example.smarthomie

class Device {

        val id: Int = 0                // Immutable
        var name: String = ""            // Mutable property
        var type: String = ""
        var status: String = ""
        var icon: String = ""
        val ownerUserId: Int = 0         // Assuming owner won't change
        var lastUpdated: Long = 0L


}
