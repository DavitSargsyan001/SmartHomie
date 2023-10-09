package com.example.smarthomie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.*;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        TextView btn = findViewById(R.id.RegisterText);
        Button but = (Button)findViewById(R.id.loginButton);

        btn.setOnClickListener(this);
        but.setOnClickListener(this);


            }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.RegisterText) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        } else if (id == R.id.loginButton) {
            startActivity(new Intent(LoginActivity.this, MyDevices.class));
        }
    }
}