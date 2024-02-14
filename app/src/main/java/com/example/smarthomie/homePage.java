package com.example.smarthomie;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class homePage extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        Button scenarioButton = (Button)findViewById(R.id.button2);
        scenarioButton.setOnClickListener(this);

        // Step 1: Find the button and set the click listener
        Button addRemoveButton = (Button)findViewById(R.id.button);
        addRemoveButton.setOnClickListener(this);

        Button myDevicesButton = (Button)findViewById(R.id.button5);
        myDevicesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button2) {
            startActivity(new Intent(homePage.this, Scenarios.class));
        }

        // Step 2: Handle the button click to start the desired activity
        if (id == R.id.button) {
            startActivity(new Intent(homePage.this, AddRemoveActivity.class));
        }

        if (id == R.id.button5) {
            startActivity(new Intent(homePage.this, MyDevicesActivity.class));
        }
    }
}


