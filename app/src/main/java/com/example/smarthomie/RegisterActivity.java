package com.example.smarthomie;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import android.util.Log;


public class RegisterActivity extends AppCompatActivity {

    private EditText emailTV;
    private EditText passwordTV;
    private Button regBtn;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "RegisterActivity";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        TextView btn = findViewById(R.id.LoginText);
        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        regBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
           }
        });

    }

    private void registerNewUser(){
        String email, password, recipient, sender, emailUsername, emailPassword;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();
        sender = "bob@gmail.com";
        emailUsername = "aovsepyan929@gmail.com";
        emailPassword = "wpjo xuyy xrhn lzha";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

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

                   Map<String, Object> user = new HashMap<>();
                   user.put("email", email);
                   user.put("listOfDevices", new ArrayList<>());

                   db.collection("Users").document(mAuth.getCurrentUser().getUid())
                           .set(user)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Log.d(TAG, "DocumentSnapshot successfully written!");
                                   Intent intent = new Intent(RegisterActivity.this, homePage.class);
                                   startActivity(intent);
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Log.w(TAG, "Error writing document", e);
                               }
                           });
                   Session session = Session.getInstance(prop,
                           new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(emailUsername, emailPassword);
                                }
                           });
                   Thread thread = new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                                String recipient = email;
                                Message message = new MimeMessage(session);
                                message.setFrom(new InternetAddress(sender));
                                message.setRecipients(
                                       Message.RecipientType.TO,
                                       InternetAddress.parse(recipient)
                               );
                                   message.setSubject("Confirmation");
                                   message.setText("Please confirm your account.");
                                   Transport.send(message);
                                   Log.d(TAG, "Success");
                           }
                           catch (Exception e) {
                               e.printStackTrace();
                           }
                       }
                   });
                   thread.start();
                   Intent intent = new Intent(RegisterActivity.this, homePage.class);
                   startActivity(intent);
               }
               else{
                   Toast.makeText(getApplicationContext(), "Registration failed! Please try again", Toast.LENGTH_LONG).show();

               }
           }
        });
    }

    private void initializeUI(){
        emailTV = findViewById(R.id.emailInput);
        passwordTV = findViewById(R.id.passwordInput);
        regBtn = findViewById(R.id.registerButton);

    }


}
