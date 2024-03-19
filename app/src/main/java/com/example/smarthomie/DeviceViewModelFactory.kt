package com.example.smarthomie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarthomie.DeviceDetailsDao
/*
class DeviceViewModelFactory(private val deviceDao: DeviceDetailsDao, private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceViewModel(deviceDao, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
 */