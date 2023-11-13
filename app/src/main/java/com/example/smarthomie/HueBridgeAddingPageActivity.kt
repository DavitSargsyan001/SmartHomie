package com.example.smarthomie

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.Manifest

class HueBridgeAddingPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.hue_bridge_adding_page)

        val searchBridgeButton: Button = findViewById<Button>(R.id.NetworkDiscoveryButton)

            searchBridgeButton.setOnClickListener(){

            }
    }
}