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

import java.util.ArrayList;
import java.util.List;

public class AddRemoveActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_remove_device);

        ImageButton btn = (ImageButton)findViewById(R.id.ibHome2);
        Button addButton = (Button)findViewById(R.id.button4);
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
               AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddRemoveActivity.this);
                mBuilder.setTitle("Choose a device");
                mBuilder.setPositiveButton("Confirm", (dialog, which) ->{});
                mBuilder.setNegativeButton("Cancel", (dialog, which) ->{} );
                mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }


}
