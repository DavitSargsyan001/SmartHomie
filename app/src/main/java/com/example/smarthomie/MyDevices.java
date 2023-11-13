package com.example.smarthomie;

import static com.example.smarthomie.R.id.ibHome3;

import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.*;

import androidx.appcompat.app.AppCompatActivity;

public class MyDevices extends AppCompatActivity {
    ImageButton myImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_devices);

        myImageButton = (ImageButton) findViewById(R.id.ibHome3);

        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(MyDevices.this, homePage.class);
                startActivity(goToHomePage);
            }
        });
    }


}
