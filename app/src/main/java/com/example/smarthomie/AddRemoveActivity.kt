package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddRemoveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_remove_device)
        val AddButton: Button = findViewById(R.id.addbutton)
        val RemoveButton: Button = findViewById(R.id.button6)
        val HomePage: ImageButton = findViewById(R.id.ibHome2)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")

        AddButton.setOnClickListener {
            checkForHueBridge(userId) {hasHueBridge, ip, username ->
                if (hasHueBridge) {// && ip != null && username != null) {

                    val intent = Intent(this, DeviceDiscoveryActivity::class.java).apply {
                        putExtra("IP_ADDRESS", ip)
                        putExtra("USERNAME", username)
                    }
                    startActivity(intent)
                } else {
                    showAlertDialog()
                }
            }
        }
        RemoveButton.setOnClickListener{
            //val intent = Intent(this, removePage::cla)
            //startActivity(intent)
        }
        HomePage.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
        }

    }

    private fun checkForHueBridge(userId: String, callback: (Boolean, String?, String?) -> Unit) {
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
                    callback(true, ip, username)
                } else {
                    // Handle the case where the Hue Bridge is not found or there is an error
                    Log.d("AddRemoveActivity", "No documents found or query failed")
                    callback(false, null, null)
                }
            }
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

}