package com.example.smarthomie;



import android.os.Bundle;
import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;


import androidx.appcompat.app.AppCompatActivity;





public class MyDevices extends AppCompatActivity {
    ImageButton myImageButton;
    Button but3;
    ToggleButton but1, but2;
    EditText num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_devices);

        myImageButton = (ImageButton) findViewById(R.id.ibHome3);
        but1 = (ToggleButton)findViewById(R.id.toggleButton);
        but2 = (ToggleButton)findViewById(R.id.toggleButton2);
        num = (EditText)findViewById(R.id.editTextNumber);
        but3 = (Button)findViewById(R.id.button4);
        myImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(MyDevices.this, homePage.class);
                startActivity(goToHomePage);
            }
        });

        but1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(but1.isChecked()) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PhilipsHueController.turnOnLight(PhilipsHueController.createTrustAllSSLContext(), "1");
                        }
                    });
                    thread.start();
                }
                else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PhilipsHueController.turnOffLight(PhilipsHueController.createTrustAllSSLContext(), "1");
                        }
                    });
                    thread.start();
                }

            }
        });

        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(but2.isChecked()) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PhilipsHueController.turnOnLight(PhilipsHueController.createTrustAllSSLContext(), "2");
                        }
                    });
                    thread.start();
                }
                else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PhilipsHueController.turnOffLight(PhilipsHueController.createTrustAllSSLContext(), "2");
                        }
                    });
                    thread.start();
                }
            }

        });

        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PhilipsHueController.setBrightness("2", num.getText().toString(), PhilipsHueController.createTrustAllSSLContext());
                    }
                });
                thread.start();
            }
        });
    }
}
