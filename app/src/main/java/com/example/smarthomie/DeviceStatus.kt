package com.example.smarthomie

interface DeviceStatus

enum class LightBulbStatus : DeviceStatus {
    ON, OFF, DIMMED, ERROR
}

enum class SmartPlugStatus : DeviceStatus {
    CONNECTED, DISCONNECTED, ERROR
}

enum class ThermostatStatus : DeviceStatus {
    HEATING, COOLING, STANDBY, OFF, ERROR
}

enum class CameraStatus : DeviceStatus {
    ACTIVE, INACTIVE, OFF, MOTION_DETECTED, ERROR
}

enum class SensorStatus : DeviceStatus {
    ACTIVE, INACTIVE, TRIGGERED, ERROR
}

enum class CoffeeMachineStatus : DeviceStatus {
    BREWING, WARMING, STANDBY, OFF, ERROR, NEEDS_REFILL, CLEANING
}