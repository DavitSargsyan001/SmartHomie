package com.example.smarthomie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deviceDetails: DeviceDetails)

    @Query("SELECT * FROM DeviceDetails WHERE deviceId = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceDetails?

    @Query("SELECT * FROM DeviceDetails")
    suspend fun getAllDevices(): List<DeviceDetails>

    @Query("SELECT * FROM DeviceDetails WHERE ownerUserID = :userId")
    suspend fun getDevicesForUser(userId: String): List<DeviceDetails>

}
