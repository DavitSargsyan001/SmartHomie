package com.example.smarthomie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        ImageButton myImageButton = (ImageButton) findViewById(R.id.ibHome2);
        Button logOutButton = (Button) findViewById(R.id.logout);

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
    }
}
