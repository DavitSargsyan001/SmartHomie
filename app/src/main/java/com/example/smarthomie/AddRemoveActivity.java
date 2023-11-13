package com.example.smarthomie;



import static com.google.android.material.internal.ContextUtils.getActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class AddRemoveActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_remove_device);

        ImageButton btn = (ImageButton)findViewById(R.id.ibHome2);
        Button addButton = (Button)findViewById(R.id.addbutton);
        String[] listItems = getResources().getStringArray(R.array.Device_List);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddRemoveActivity.this, homePage.class));
            }
        });

    addButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("AddDevice", "Add button clicked"); // Log at the beginning of the onClick

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d("AddDevice", "Firestore db Initialized?"); // Log at the beginning of the onClick
            db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
               if(task.isSuccessful() && task.getResult() != null){
                   Log.d("AddDevice", "Task was sucesfull?"); // Log at the beginning of the onClick
                   DocumentSnapshot document = task.getResult();
                   List<String> deviceIDs = (List<String>) document.get("listOfDevices");

                   if(deviceIDs == null || deviceIDs.isEmpty())
                   {
                       Log.d("AddDevice", "No Device IDs found, prompt user to add HUE bridge");
                       // Prompt the user to add a HUE bridge since there are no devices
                       AlertDialog dialog = createDialog(); // Presuming createDialog() creates the appropriate dialog
                       dialog.show();
                   }
                   else if(deviceIDs != null ) {
                       Log.d("AddDevice", "No Device IDs found, prompt user to add HUE bridge"); // Log at the beginning of the onClick
                       for (String deviceID : deviceIDs) {
                           Log.d("AddDevice", "Processing device ID: " + deviceID);
                           db.collection("Devices").document(deviceID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                               @Override
                               public void onSuccess(DocumentSnapshot deviceDocument) {
                                   if (deviceDocument.exists()) {
                                       Log.d("AddDevice", "Device document exists"); // Log at the beginning of the onClick
                                       String deviceType = deviceDocument.getString("type");
                                       if ("HUE_BRIDGE".equals(deviceType)) {
                                           Log.d("AddDevice", "HUE bridge already exists in user's account"); // Log at the beginning of the onClick
                                           //This device is a hue bridge. go to adding devices
                                           //AlertDialog dialog = createDialog();
                                       } else {
                                           Log.d("AddDevice", "User does not have Hue bridge and we will force him to add it"); // Log at the beginning of the onClick
                                           //Force user to add Hue bridge to the App
                                           AlertDialog dialog = createDialog();
                                           dialog.show();
                                       }
                                   }
                               }
                           });
                       }
                   }else{
                       Log.d("AddDevice", "There are no device IDs"); // Log at the beginning of the onClick
                       AlertDialog dialog = createDialog();
                       dialog.show();
                   }
               }else{
                   //deal with error
                   Log.d("AddDevice", "Task was not successful!"); // Log at the beginning of the onClick
                   String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error fetching user details.";
                   showErrorDialog(errorMessage);
               }
            });
        }
    });





    }

    AlertDialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to add HUE bridge before adding any devices");
        builder.setPositiveButton("Add HUE bridge", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AddRemoveActivity.this,"Redirecting",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddRemoveActivity.this, HueBridgeAddingPageActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AddRemoveActivity.this,"Canceled",Toast.LENGTH_LONG).show();
            }
        });
        return builder.create();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



}
