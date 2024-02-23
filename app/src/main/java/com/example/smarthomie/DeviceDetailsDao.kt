package com.example.smarthomie

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(deviceDetails: DeviceDetails)

    @Query("SELECT * FROM DeviceDetails WHERE deviceId = :deviceId")
     fun getDeviceById(deviceId: String): DeviceDetails?

    @Query("SELECT * FROM DeviceDetails")
     fun getAllDevices(): LiveData<List<DeviceDetails>>

    @Query("SELECT * FROM DeviceDetails WHERE ownerUserID = :userId")
     fun getDevicesForUser(userId: String): LiveData<List<DeviceDetails>>

    @Query("DELETE FROM DeviceDetails WHERE deviceId = :deviceId")
     fun deleteDevice(deviceId: String)

}
