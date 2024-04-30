package com.example.smarthomie

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarthomie.databinding.MyDevices2Binding
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class MyDevicesActivity2 : AppCompatActivity(), DeviceActionListener  {
    private lateinit var binding: MyDevices2Binding
    private lateinit var adapter: DeviceAdapter
    private lateinit var deviceControlService: DeviceControlService
    private val viewModel: DeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyDevices2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeButton: ImageButton = findViewById(R.id.ibHome2)

        homeButton.setOnClickListener{
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }

        val cancelButton : Button = findViewById(R.id.button10)

        cancelButton.setOnClickListener {
            val intent = Intent(this, MyGroup::class.java)
            startActivity(intent)
        }

        deviceControlService = DeviceControlService()
        setupRecyclerView()
        observeDevices()

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
                    AlertDialog.Builder(this@MyDevicesActivity2)
                        .setTitle("Choose Group")
                        .setPositiveButton("Confirm") { dialog, which ->
//                            // Proceed with deletion
//                            lifecycleScope.launch {
//                                adapter.removeDeviceAtPosition(deviceToDelete, position)
//                                // Update UI after network operation
                                adapter.notifyItemChanged(position)
//                            }
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            // Cancel the deletion and reset the item
                            adapter.notifyItemChanged(position)
                        }
                        .setOnCancelListener {
                            // In case the dialog is dismissed, reset the item
                            adapter.notifyItemChanged(position)
                        }
                        .setSingleChoiceItems(
                            arrayOf("Living Room", "Bedroom"), 0
                        ) {
                            dialog, which ->
                            println("faf")
                        }
                        .show()
                }

                //adapter.removeDeviceAtPosition(position) // Call your method to remove the device from Firestore
                //adapter.notifyItemRemoved(position) // Notify the adapter that an item was removed
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.devicesRecyclerView)

    }

    private fun setupRecyclerView() {

        val clickListener: (DeviceDetails) -> Unit = {device ->
            Toast.makeText(this, "Clicked on device: ${device.name}", Toast.LENGTH_SHORT).show()


        }

        adapter = DeviceAdapter(mutableListOf(),  AdapterContext.MY_DEVICES2, this, clickListener)
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
    }
}