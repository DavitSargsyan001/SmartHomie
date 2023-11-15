package com.example.smarthomie;



import android.os.Bundle;
import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;





public class MyDevices extends AppCompatActivity {
    ImageButton myImageButton;
    Button but;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_devices);

        myImageButton = (ImageButton) findViewById(R.id.ibHome3);
        but = (Button)findViewById(R.id.button7);
        myImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(MyDevices.this, homePage.class);
                startActivity(goToHomePage);
            }
        });

        but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PhilipsHueController.turnOnLight(PhilipsHueController.createTrustAllSSLContext());
                    }
                });
                thread.start();
            }
        });

    }
}
