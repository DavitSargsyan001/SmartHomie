package com.example.smarthomie;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        TextView btn = findViewById(R.id.LoginText);
        Button but = (Button)findViewById(R.id.registerButton);

        btn.setOnClickListener(this);
        but.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.LoginText) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
        else if (id == R.id.registerButton) {
            startActivity(new Intent(RegisterActivity.this, MyDevices.class));
        }
    }
}
