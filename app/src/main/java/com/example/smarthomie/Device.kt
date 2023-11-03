package com.example.smarthomie

import com.example.smarthomie.DeviceStatus

class Device {

        //val id: Int = 0                // Immutable
        var name: String = ""            // Mutable property
        var type: DeviceType? = null
        var status: DeviceStatus? = null
        var icon: String = ""
        //val ownerUserId: Int = 0         // Assuming owner won't change
        //var lastUpdated: Long = 0L

        enum class DeviceType(val displayName: String) {
                LIGHTBULB("Light Bulb"),
                SMART_PLUG("Smart Plug"),
                THERMOSTAT("Thermostat"),
                CAMERA("Camera"),
                SENSOR("Sensor"),
                COFFEEMACHINE("Coffee Machine"),
                HUE_BRIDGE("Hue Bridge");
        }

        fun updateStatus(newStatus: DeviceStatus){
                when (type) {
                        DeviceType.LIGHTBULB -> {
                                if (newStatus is LightBulbStatus) {
                                        status = newStatus
                                } else {
                                        throw IllegalArgumentException("Invalid status for a light bulb")
                                }
                        }
                        DeviceType.SMART_PLUG -> {
                                if (newStatus is SmartPlugStatus) {
                                        status = newStatus
                                } else {
                                        throw IllegalArgumentException("Invalid status for a smart plug")
                                }
                        }
                        DeviceType.CAMERA -> {
                                if (newStatus is CameraStatus) {
                                        status = newStatus
                                } else {
                                        throw IllegalArgumentException("Invalid status for a camera")
                                }
                        }
                        DeviceType.THERMOSTAT -> {
                                if (newStatus is ThermostatStatus) {
                                        status = newStatus
                                } else {
                                        throw IllegalArgumentException("Invalid status for a thermostat")
                                }
                        }
                        DeviceType.SENSOR -> {
                                if (newStatus is SensorStatus) {
                                        status = newStatus
                                } else {
                                        throw IllegalArgumentException("Invalid status for a sensor")
                                }
                        }
                        DeviceType.COFFEEMACHINE -> {
                                if (newStatus is CoffeeMachineStatus) {
                                        status = newStatus
                                } else {
                                        throw IllegalArgumentException("Invalid status for a coffee machine")
                                }
                        }
                        DeviceType.HUE_BRIDGE -> {
                                if (newStatus is HueBridgeStatus) {
                                        status = newStatus
                                } else {
                                        throw IllegalArgumentException("Invalid status for a coffee machine")
                                }
                        }
                        else -> {
                                throw IllegalArgumentException("Unknown device type")
                        }
                }
        }




}
