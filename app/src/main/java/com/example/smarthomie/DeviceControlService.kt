package com.example.smarthomie

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class DeviceControlService {

    fun toggleDeviceOnOff(deviceId: String, isOn: Boolean, hueBridgeIp: String, hueUsername: String, callback: (Boolean) -> Unit) {
        Log.d("DeviceControlService", "bridge IP: $hueBridgeIp  bridge username: $hueUsername device numeric ID: $deviceId")
        val url = "http://$hueBridgeIp/api/$hueUsername/lights/$deviceId/state"
        Log.d("DeviceControlService","is it ON? : $isOn")
        val requestBody = JSONObject().apply {
            put("on", isOn)
        }.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        })
    }

    fun setBrightness(deviceId: String, brightness: Int, hueBridgeIp: String, hueUsername: String, callback: (Boolean) -> Unit) {
        val url = "http://$hueBridgeIp/api/$hueUsername/lights/$deviceId/state"
        val requestBody = JSONObject().apply {
            put("bri", brightness)  // "bri" is the Hue API key for brightness and expects a value from 0 to 254.
        }.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DeviceControlService", "Failed to set brightness", e)
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("DeviceControlService", "Brightness set successfully")
                    callback(true)
                } else {
                    Log.e("DeviceControlService", "Failed to set brightness")
                    callback(false)
                }
            }
        })
    }
}