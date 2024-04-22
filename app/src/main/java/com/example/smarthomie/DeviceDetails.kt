package com.example.smarthomie

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class DeviceType {
    HUE_BRIDGE , THERMOSTAT, LIGHTBULB, SMART_PLUG
}

@Entity
@Parcelize
data class DeviceDetails(
    @PrimaryKey var deviceId: String = "", // Unique ID for the device
    @get:PropertyName("Name: ") @set:PropertyName("Name: ") var name: String? = null,
    @get:PropertyName("Status: ") @set:PropertyName("Status: ") var status: String? = null,
    //var Status : String? = null, // Status, e.g., "Connected"
    @get:PropertyName("Type: ") @set:PropertyName("Type: ") var type: String? = null,
    //var Type : String = "",
    @get:PropertyName("IP") @set:PropertyName("IP") var ip: String? = null,
    //var IP : String = "", // IP address of the device
    @get:PropertyName("hueBridgeUsername: ") @set:PropertyName("hueBridgeUsername: ") var hueBridgeUsername: String? = null,
    //var hueBridgeUsername : String = "", // Username for Hue Bridge
    @get:PropertyName("ownerUserID") @set:PropertyName("ownerUserID") var ownerUserID: String? = null,
    //var ownerUserID : String = "",
    @get:PropertyName("isSelected") @set:PropertyName("isSelected") var isSelected: Boolean = false,
    //var isSelected : Boolean = false
    @get:PropertyName("id") @set:PropertyName("id") var id: String? = null,
    @get:PropertyName("documentID") @set:PropertyName("documentID") var documentID: String? = null,
    //@get:PropertyName("numericID") @set:PropertyName("numericID") var numericID: String? = null,

) : Parcelable
