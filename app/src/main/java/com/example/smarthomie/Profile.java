package com.example.smarthomie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Profile extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        auth = FirebaseAuth.getInstance();
        userEmailTextView = findViewById(R.id.userEmailTextView);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userEmailTextView.setText(user.getEmail());  // Set the email to the TextView
        } else {
            userEmailTextView.setText("No user signed in");  // Handle case where no user is signed in
        }

        ImageButton myImageButton = (ImageButton) findViewById(R.id.ibHome2);
        Button logOutButton = (Button) findViewById(R.id.logout);
        //Button changeEmailButton = (Button) findViewById(R.id.changeEmail);
        Button changePasswordButton = (Button) findViewById(R.id.changePassword);

        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(Profile.this, homePage.class);
                startActivity(goToHomePage);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logOut = new Intent(Profile.this, LoginActivity.class);
                startActivity(logOut);
            }
        });

        // Change email listener
       /* changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the ChangeEmailActivity
                Intent intent = new Intent(Profile.this, ChangeEmailActivity.class);
                startActivity(intent); // Start the new activity
            }
        }); */

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        //steic grdon
        TextView accountCreationDateTextView = findViewById(R.id.accountCreationDateTextView);

        if (user != null) {
            // Get user creation timestamp and format it
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String creationDate = dateFormat.format(new Date(user.getMetadata().getCreationTimestamp()));

            // Set the formatted date in TextView
            accountCreationDateTextView.setText("Account Created: " + creationDate);
        } else {
            accountCreationDateTextView.setText("No user signed in");
        }


    }

// CHANGE PASSWORD FUNCTION

    private void showChangePasswordDialog() {
        // Inflate the dialog_change_password.xml layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);

        final EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        final EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);

        new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPassword = etOldPassword.getText().toString().trim();
                        String newPassword = etNewPassword.getText().toString().trim();
                        changeUserPassword(oldPassword, newPassword);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void changeUserPassword(String oldPassword, String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && !newPassword.isEmpty()) {
            // Re-authenticate the user
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Proceed with password update
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(Profile.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Profile.this, "Password update failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(Profile.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

//CHANGE EMAIL FUNCTION

}


