package com.example.smarthomie;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
public class AddRemoveActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_remove_device);

        ImageButton btn = (ImageButton)findViewById(R.id.ibHome2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddRemoveActivity.this, homePage.class));
            }
        });
    }
}
