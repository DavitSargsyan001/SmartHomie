package com.example.smarthomie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceDetails(
    @PrimaryKey val deviceId: String, // Unique ID for the device
    val name: String, // Name of the device
    val status: String, // Status, e.g., "Connected"
    val type: String,
    val ip: String, // IP address of the device
    val hueBridgeUsername: String, // Username for Hue Bridge
    val ownerUserID: String
)