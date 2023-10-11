package com.example.smarthomie;

import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.*;
import androidx.appcompat.app.AppCompatActivity;

public class homePage extends AppCompatActivity implements View.OnClickListener {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_page);

            Button scenarioButton = (Button)findViewById(R.id.button2);
            scenarioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.button2) {
                startActivity(new Intent(homePage.this, Scenarios.class));
            }
        }
    }

