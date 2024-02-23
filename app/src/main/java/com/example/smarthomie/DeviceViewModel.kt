package com.example.smarthomie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarthomie.DeviceDetailsDao
/*
class DeviceViewModel(private val deviceDao: DeviceDetailsDao) : ViewModel() {
    val devices: LiveData<List<DeviceDetails>> = deviceDao.getAllDevices()

}
*/
class DeviceViewModel(private val deviceDao: DeviceDetailsDao, private val userId: String) : ViewModel() {
    private val _devices = MediatorLiveData<List<DeviceDetails>>()
    val devices: LiveData<List<DeviceDetails>> = _devices

    init {
        _devices.addSource(deviceDao.getDevicesForUser(userId)) { devices ->
            _devices.value = devices
        }
    }
}