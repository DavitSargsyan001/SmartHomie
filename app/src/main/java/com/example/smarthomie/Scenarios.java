package com.example.smarthomie;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Scenarios extends AppCompatActivity {
    ImageButton myImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenarios_page);

        myImageButton = (ImageButton) findViewById(R.id.ibHome);

        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(Scenarios.this, homePage.class);
                startActivity(goToHomePage);
            }
        });
    }
}
