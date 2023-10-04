package com.example.smarthomie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        TextView btn = findViewById(R.id.LoginText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
                    public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

        });
    }
}
