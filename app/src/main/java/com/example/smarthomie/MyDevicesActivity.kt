package com.example.smarthomie

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.example.smarthomie.databinding.MyDevicesBinding
import com.example.smarthomie.DeviceViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smarthomie.DeviceControlService


class MyDevicesActivity : AppCompatActivity(), DeviceActionListener {
    private lateinit var binding: MyDevicesBinding
    private lateinit var adapter: DeviceAdapter
    private lateinit var deviceControlService: DeviceControlService
    private val viewModel: DeviceViewModel by viewModels ()
    /*
    {
        //DeviceViewModel(DatabaseBuilder.getInstance(application).deviceDetailsDao())
        val deviceDao = DatabaseBuilder.getInstance(application).deviceDetailsDao()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")
        DeviceViewModelFactory(deviceDao, userId)
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeButton: ImageButton = findViewById(R.id.ibHome3)

        homeButton.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }
        deviceControlService = DeviceControlService()
        setupRecyclerView()
        observeDevices()

        ////Adding swipe method for each item in the recycler view
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false // We don't want move functionality here
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition // Get the swiped item position
                    val deviceToDelete = adapter.getDeviceAtPosition(position)

                    // Show confirmation dialog
                    AlertDialog.Builder(this@MyDevicesActivity)
                        .setTitle("Delete Device")
                        .setMessage("Are you sure you want to delete this device?")
                        .setPositiveButton("Delete") { dialog, which ->
                            // Proceed with deletion
                            lifecycleScope.launch {
                                adapter.removeDeviceAtPosition(deviceToDelete, position)
                                // Update UI after network operation
                                adapter.notifyItemRemoved(position)
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            // Cancel the deletion and reset the item
                            adapter.notifyItemChanged(position)
                        }
                        .setOnCancelListener {
                            // In case the dialog is dismissed, reset the item
                            adapter.notifyItemChanged(position)
                        }
                        .show()
                }

                //adapter.removeDeviceAtPosition(position) // Call your method to remove the device from Firestore
                //adapter.notifyItemRemoved(position) // Notify the adapter that an item was removed
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.devicesRecyclerView)




       ////End of adding swipe method

    }

    private fun setupRecyclerView() {

        val clickListener: (DeviceDetails) -> Unit = {device ->
            Toast.makeText(this, "Clicked on device: ${device.name}", Toast.LENGTH_SHORT).show()


        }

        adapter = DeviceAdapter(mutableListOf(),  AdapterContext.MY_DEVICES, this, clickListener)
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.devicesRecyclerView.adapter = adapter
    }

    private fun observeDevices(){
        viewModel.devicesLiveData.observe(this, Observer{ devices ->
        // Update your RecyclerView adapter with the list of devices
        adapter.submitList(devices.toMutableList())
        //(binding.devicesRecyclerView.adapter as DeviceAdapter).submitList(devices)
    })
    }

    override fun onPerformQuickAction(device: DeviceDetails) {
        /*
        Toast.makeText(this, "Quick action for ${device.name}", Toast.LENGTH_SHORT).show()
        val hueBridgeIP = device.ip
        val hueUsername = device.hueBridgeUsername
        Log.d("MyDevicesActivity", "IP: ${device.ip}  Hue Username: ${device.hueBridgeUsername}  Device Status: ${device.status}")
        val url = "http://$hueBridgeIP/api/$hueUsername/lights"
*/
        //{
            val isOn = device.status == "On" // Determine the current state based on your status representation
            deviceControlService.toggleDeviceOnOff(device.deviceId!!, !isOn, device.ip!!, device.hueBridgeUsername!!) { success ->
                runOnUiThread {
                    if (success) {
                        // Update device status in UI accordingly
                        device.status = if (isOn) "Off" else "On"
                        Toast.makeText(this, "Device toggled successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to toggle device", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
