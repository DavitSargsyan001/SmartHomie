package com.example.smarthomie

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

enum class DeviceType {
    HUE_BRIDGE , THERMOSTAT, LIGHTBULB, SMART_PLUG
}
/*
@Entity
data class DeviceDetails(
    @PrimaryKey var deviceId: String = "", // Unique ID for the device
    var Name: String = "", // Name of the device
    var Status: String? = null, // Status, e.g., "Connected"
    var Type: String = "",
    var IP: String = "", // IP address of the device
    var hueBridgeUsername: String = "", // Username for Hue Bridge
    var ownerUserID: String = "",
    var isSelected: Boolean = false
)
*/
@Entity
data class DeviceDetails(
    @PrimaryKey
    var deviceId: String = "", // You'll need to assign this manually since it's not a field in the Firestore document
    @PropertyName("Name") var name: String = "",
    @PropertyName("Status") var status: String? = null,
    @PropertyName("Type") var type: String = "",
    @PropertyName("IP") var ip: String = "",
    @PropertyName("hueBridgeUsername") var hueBridgeUsername: String = "",
    @PropertyName("ownerUserID") var ownerUserID: String = "",
    @PropertyName("isSelected") var isSelected: Boolean = false,
    //@Ignore // Since isSelected is not a field in Firestore, we tell Room to ignore it
    //var isSelected: Boolean = false
)