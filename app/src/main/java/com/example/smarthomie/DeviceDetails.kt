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
    @PrimaryKey var deviceId: String = "",
    @get:PropertyName("Name: ") @set:PropertyName("Name: ") var name: String? = null,
    @get:PropertyName("Status: ") @set:PropertyName("Status: ") var status: String? = null,
    @get:PropertyName("Type: ") @set:PropertyName("Type: ") var type: String? = null,
    @get:PropertyName("IP") @set:PropertyName("IP") var ip: String? = null,
    @get:PropertyName("hueBridgeUsername: ") @set:PropertyName("hueBridgeUsername: ") var hueBridgeUsername: String? = null,
    @get:PropertyName("ownerUserID") @set:PropertyName("ownerUserID") var ownerUserID: String? = null,
    @get:PropertyName("isSelected") @set:PropertyName("isSelected") var isSelected: Boolean = false,
    @get:PropertyName("id") @set:PropertyName("id") var id: String? = null,
    @get:PropertyName("documentID") @set:PropertyName("documentID") var documentID: String? = null,
    //@get:PropertyName("numericID") @set:PropertyName("numericID") var numericID: String? = null,

) : Parcelable
