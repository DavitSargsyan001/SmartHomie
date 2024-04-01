package com.example.smarthomie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarthomie.DeviceDetailsDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/*
class DeviceViewModel(private val deviceDao: DeviceDetailsDao) : ViewModel() {
    val devices: LiveData<List<DeviceDetails>> = deviceDao.getAllDevices()

}

class DeviceViewModel(private val deviceDao: DeviceDetailsDao, private val userId: String) : ViewModel() {
    private val _devices = MediatorLiveData<List<DeviceDetails>>()
    val devices: LiveData<List<DeviceDetails>> = _devices

    init {
        _devices.addSource(deviceDao.getDevicesForUser(userId)) { devices ->
            _devices.value = devices
        }
    }
}

class DeviceViewModel(private val deviceDao: DeviceDetailsDao, private val userId: String) : ViewModel() {
    private val _devicesLiveData = MutableLiveData<List<DeviceDetails>>()
    val devicesLiveData: LiveData<List<DeviceDetails>> = _devicesLiveData

    fun fetchDevicesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Devices").whereEqualTo("ownerUserID", userId)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val deviceList = queryDocumentSnapshots.documents.mapNotNull { document ->
                    document.toObject(DeviceDetails::class.java)?.apply {
                        deviceId = document.id // Ensure your DeviceDetails class has a deviceId property to assign this
                        Log.d("DeviceViewModel", "Device ID that we get $deviceId  or ${document.id}")
                    }
                }
                _devicesLiveData.postValue(deviceList)
            }
            .addOnFailureListener { e ->
                Log.e("DeviceViewModel", "Error fetching devices", e)
            }
    }
}
*/

class DeviceViewModel : ViewModel() {
    private val _devicesLiveData = MutableLiveData<List<DeviceDetails>>()
    val devicesLiveData: LiveData<List<DeviceDetails>> = _devicesLiveData
    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalStateException("User not logged in")

    init {
        fetchDevicesFromFirestore()
    }

    private fun fetchDevicesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Devices").whereEqualTo("ownerUserID", userId)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val deviceList = queryDocumentSnapshots.documents.mapNotNull { document ->
                    document.toObject(DeviceDetails::class.java)?.apply {
                        deviceId = document.getString("numericID") ?: ""
                        Log.d("DeviceViewModel", "Fetched device: Name=$name, Status=$status, Type=$type")
                    }
                }
                _devicesLiveData.postValue(deviceList)
            }
            .addOnFailureListener { e ->
                Log.e("DeviceViewModel", "Error fetching devices", e)
            }
    }
}