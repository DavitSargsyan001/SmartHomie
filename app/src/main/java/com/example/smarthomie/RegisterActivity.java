package com.example.smarthomie;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText emailTV;
    private EditText passwordTV;
    private Button registrate;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        regBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

    }

    private void registerNewUser(){
        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                   progressBar.setVisibility(View.GONE);

                   Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                   startActivity(intent);
               }
               else{
                   Toast.makeText(getApplicationContext(), "Registration failed! Please try again", Toast.LENGTH_LONG).show();
                   progressBar.setVisibility(View.GONE);
               }
           }
        });
    }

    private void initializeUI(){
        emailTV = findViewById(R.id.emailInput);
        passwordTV = findViewById(R.id.passwordInput);
        regBtn = findViewById(R.id.registerButton);

    }

   /* @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.LoginText) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
        else if (id == R.id.registerButton) {
            startActivity(new Intent(RegisterActivity.this, MyDevices.class));
        }
    }

*/

}
