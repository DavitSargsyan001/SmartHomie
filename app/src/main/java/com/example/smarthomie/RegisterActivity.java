package com.example.smarthomie;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private EditText password;
    private Button register;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        TextView btn = findViewById(R.id.LoginText);
        Button but = (Button)findViewById(R.id.registerButton);

        btn.setOnClickListener(this);
        but.setOnClickListener(this);

        email = findViewById(R.id.emailInput);
        password =  findViewById(R.id.passwordInput);
        register = findViewById(R.id.registerButton);

        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty( txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText( RegisterActivity.this, "Empty crcedentials!", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText( RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                } else{
                    registerUser(txt_email , txt_password);
                }

            }
        });
    }

    private void registerUser(String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this ,new onCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText( RegisterActivity.this, "Registering user successfull" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText( RegisterActivity.this, "Registering failed!" , Toast.LENGTH_SHORT).show();
                }
            }
        })

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
