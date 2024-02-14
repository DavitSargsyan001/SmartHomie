package com.example.smarthomie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarthomie.DeviceDetailsDao

class DeviceViewModelFactory(private val deviceDao: DeviceDetailsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceViewModel(deviceDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}