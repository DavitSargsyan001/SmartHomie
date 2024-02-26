package com.example.smarthomie

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
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
            checkForHueBridge(userId) {hasHueBridge ->
                if (hasHueBridge) {
                    val intent = Intent(this, DeviceDiscoveryActivity::class.java)
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

    private fun checkForHueBridge(userId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val devicesRef = db.collection("devices")

        devicesRef.whereEqualTo("ownerUserID", userId)
            .whereEqualTo("type", "HueBridge")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val hasHueBridge = task.result != null && !task.result!!.isEmpty
                    callback(hasHueBridge)
                } else {
                    callback(false)
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