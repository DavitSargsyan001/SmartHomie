package com.example.smarthomie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

   private EditText email, password;
   private Button login;

   private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        TextView btn = findViewById(R.id.RegisterText);
        Button but = (Button)findViewById(R.id.loginButton);

        btn.setOnClickListener(this);
        but.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email)) {
                    Toast.makeText(LoginActivity.this, "Please enter email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    loginUser(txt_email, txt_password);
                }
            }
        });

    }

            private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, homePage.class));
                }
                else {
                    Toast.makeText(LoginActivity.this, "Login failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
            }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.RegisterText) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }
}