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
                COFFEEMACHINE("Coffee Machine");
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
                        else -> {
                                throw IllegalArgumentException("Unknown device type")
                        }
                }
        }


        fun discoverDevices() {
                val ssdpRequest =
                        "M-SEARCH * HTTP/1.1\r\n" +
                        "HOST: 239.255.250:1900\r\n" +
                        "MAN: \"ssdp:discover\"\r\n" +
                        "MX: 1\r\n" +
                        "ST: ssdp:all\r\n\r\n"

                val sendData = ssdpRequest.toByteArray(Charset.UTF_8)

                val sendPacket = DatagramPacket(
                        sendData,
                        sendData.size,
                        InetAddress.getByName("239.255.255.250"),
                        1900
                )

                val socket = DatagramSocket()
                socket.send(sendPacket)
        }

        fun listenForResponses(){
                val socket = DatagramSocket(1900)

                while (true){ // Continue this in a loop to keep listening for responses
                        val buffer = ByteArray(1024)
                        val packet = DatagramPacket(buffer, buffer.size)

                        socket.receive(packet)

                        val response = String(packet.data, 0, packet.length)

                        //here we could parse the response to see if it's from a device we are interested in (HUE bridge)
                        //Typically, you'll look for specific header or content in the response to identify the device
                }
        }




}
