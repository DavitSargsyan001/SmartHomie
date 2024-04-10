package com.example.smarthomie

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        auth = FirebaseAuth.getInstance()

        val newEmailEditText: EditText = findViewById(R.id.editTextNewEmail)
        val changeEmailButton: Button = findViewById(R.id.buttonChangeEmail)

        changeEmailButton.setOnClickListener {
            val newEmail = newEmailEditText.text.toString().trim()
            if (newEmail.isNotEmpty()) {
                changeEmail(auth.currentUser, newEmail)
            } else {
                Toast.makeText(this, "Please enter a new email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeEmail(user: FirebaseUser?, newEmail: String) {
        user?.let {
            it.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email updated successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } ?: run {
            Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show()
        }
    }
}