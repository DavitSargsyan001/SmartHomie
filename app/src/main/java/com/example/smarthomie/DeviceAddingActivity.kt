package com.example.smarthomie

import androidx.appcompat.app.AppCompatActivity


import android.os.Bundle
import android.provider.CalendarContract.Instances
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class DeviceAddingActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle? ){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_adding_page)

        val deviceName: EditText = findViewById(R.id.deviceName)
        val typeSpinner: Spinner = findViewById(R.id.types_of_devices)
        val save: Button = findViewById(R.id.save)

        ArrayAdapter.createFromResource(
            this,
            R.array.Device_List,
            android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter
        }

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedType = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Typically not used, so can be left empty unless you have a specific need
            }
        }

        save.setOnClickListener {
            val selectedTypePosition = typeSpinner.selectedItemPosition
            if (selectedTypePosition <= 0)
            {
                AlertDialog.Builder(this@DeviceAddingActivity)
                    .setTitle("Type Missing")
                    .setMessage("Please select the type of device to be added")
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                // Proceed with handling device addition into, local and remote databases
                // Also save it into My devices page

            }
        }




    }



    //

}