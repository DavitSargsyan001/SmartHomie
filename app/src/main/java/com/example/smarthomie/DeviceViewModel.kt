package com.example.smarthomie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarthomie.DeviceDetailsDao

class DeviceViewModel(private val deviceDao: DeviceDetailsDao) : ViewModel() {
    val devices: LiveData<List<DeviceDetails>> = deviceDao.getAllDevices()

}