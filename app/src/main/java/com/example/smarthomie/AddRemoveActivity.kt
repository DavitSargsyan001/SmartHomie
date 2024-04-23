package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AddRemoveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_remove_device)
        val addButton: Button = findViewById(R.id.addbutton)

        val homePage: ImageButton = findViewById(R.id.ibHome2)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val addBridgeBtn: Button = findViewById(R.id.button7)

        addBridgeBtn.setOnClickListener {
            val intent = Intent(this, HueBridgeAddingPageActivity::class.java)
            startActivity(intent)
        }


        addButton.setOnClickListener {
            checkForHueBridge(userId)
        }

        homePage.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }

    }

    private fun checkForHueBridge(userId: String){//, callback: (Boolean, String?, String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val listOfDeviceIds = userDocument.get("listOfDevices") as? List<String> ?: listOf()
                if (!listOfDeviceIds.isNullOrEmpty()){
                    findHueBridges(listOfDeviceIds, db) { hueBridges ->
                        if (hueBridges.isNotEmpty()) {
                            showHueBridgeSelectionDialog(hueBridges)
                        } else {
                            showAlertDialog()
                        }
                    }
                } else {
                    Log.d("AddRemoveActivity", "User does not have any devices")
                    showAlertDialog()
                }

            }
            .addOnFailureListener{exception ->
                Log.d("AddRemoveActivity", "Something wrong with accessing Users collection")
                runOnUiThread {
                    Toast.makeText(this, "Failed to fetch devices. Please try again later.", Toast.LENGTH_LONG).show()
                }
            }

        /*
        val db = FirebaseFirestore.getInstance()
        val devicesRef = db.collection("Devices")
        Log.d("AddingPage", "This is the userID: $userId")

        devicesRef.get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d("AddRemoveActivity", "${document.id} => ${document.data}")
            }
        }

        devicesRef.whereEqualTo("ownerUserID", userId)
            .whereEqualTo("Type", "HueBridge")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful ) {//&& !task.result!!.isEmpty) {//
                    // Assuming the first document is the desired one as per previous assumptions
                    Log.d("AddRemoveActivity", "Documents found: ${task.result!!.size()}")
                    val document = task.result!!.documents[0]
                    val ip =
                        document.getString("IP") // Replace "ip" with the actual field name for the IP address
                    val username =
                        document.getString("hueBridgeUsername") // Replace "username" with the actual field name for the Hue bridge username
                    Log.d("AddRemoveActivity", "This is the IP of the bridge: $ip")
                    Log.d("AddRemoveActivity", "This is the username of the bridge: $username")
                    callback(true, ip, username)
                } else {
                    // Handle the case where the Hue Bridge is not found or there is an error
                    Log.d("AddRemoveActivity", "No documents found or query failed")
                    callback(false, null, null)
                }
            }
        */
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Hue Bridge Found")
        builder.setMessage("You need to connect a Hue Bridge to continue. Would you like to add one now?")
        builder.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(this, HueBridgeAddingPageActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun findHueBridges(listOfDeviceIds: List<String>, db: FirebaseFirestore, callback: (List<DocumentSnapshot>) -> Unit) {
        val hueBridges = mutableListOf<DocumentSnapshot>()
        val deviceRef = db.collection("Devices")

        val tasks = listOfDeviceIds.map { deviceId ->
            deviceRef.document(deviceId).get()
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    if ("HueBridge".equals(document.getString("Type: "), ignoreCase = true)) {
                        hueBridges.add(document)
                    }
                }
                callback(hueBridges)
            }
            .addOnFailureListener { exception ->
                Log.e("AddRemoveActivity", "Error fetching devices", exception)
                callback(hueBridges) // Return empty list or handle the error as needed
            }
    }


    private fun showHueBridgeSelectionDialog(hueBridges: List<DocumentSnapshot>) {
        val bridgeNames = hueBridges.map { it.getString("Name: ") ?: "Unknown Bridge" }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select a Hue Bridge")
            .setItems(bridgeNames.toTypedArray()) { dialog, which ->
                val selectedBridge = hueBridges[which]
                // TODO: Proceed with the selected Hue Bridge
                val ip = selectedBridge.getString("IP")
                val username = selectedBridge.getString("hueBridgeUsername: ")
                Log.d("AddRemoveActivity", "Selected Bridge IP: $ip, Username: $username")
                val intent = Intent(this@AddRemoveActivity, DeviceDiscoveryActivity::class.java).apply {
                    putExtra("IP_ADDRESS", ip)
                    putExtra("USERNAME", username)
                }
                startActivity(intent)
                // Save the selected bridge information or pass it to the next activity
            }
        builder.show()
    }


}