package com.example.smarthomie;



import static com.google.android.material.internal.ContextUtils.getActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;

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
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
               if(task.isSuccessful() && task.getResult() != null){
                   DocumentSnapshot document = task.getResult();
                   List<String> deviceIDs = (List<String>) document.get("listOfDevices");

                   for (String deviceID : deviceIDs) {
                       db.collection("Devices").document(deviceID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                           public void onSuccess(DocumentSnapshot deviceDocument) {
                                if(deviceDocument.exists()) {
                                    String deviceType = deviceDocument.getString("type");
                                    if("HUE_BRIDGE".equals(deviceType)){
                                        //This device is a hue bridge. go to adding devices
                                    } else{
                                        //Force user to add Hue bridge to the App
                                    }
                                }
                            }
                       });
                   }
               }else{
                   //deal with error
               }
            });
        }
    });

    }




}
