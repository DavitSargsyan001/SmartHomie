package com.example.smarthomie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MyGroup extends AppCompatActivity {

    private List<String> lightURls = new ArrayList<>();
    private  List<String> plugURLs = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseFirestore db;
    String userId;
    private String numericID;
    NestAPI nestAPI = new NestAPI();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_home);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get current user info
        userId = auth.getCurrentUser().getUid();

        // Retrieve devices associated with the user
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> deviceIds = (List<String>) documentSnapshot.get("listOfDevices");
                if (deviceIds != null && !deviceIds.isEmpty()) {
                    // Retrieve device info for each device
                    for (String deviceId : deviceIds) {
                        DocumentReference deviceRef = db.collection("Devices").document(deviceId);
                        deviceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot deviceSnapshot) {
                                if (deviceSnapshot.exists()) {
                                    // Get device ID for each device from user
                                    numericID = deviceSnapshot.getString("numericID");
                                    if (numericID != null) {
                                        // Create URLS based on the  type of devices
                                        String IP = deviceSnapshot.getString("IP");
                                        String hueBridgeUsername = deviceSnapshot.getString("hueBridgeUsername: ");
                                        String deviceType = deviceSnapshot.getString("Type: ");
                                        String deviceName = deviceSnapshot.getString("Name: ");

                                        if (deviceType != null) {
                                            // Based on device type separate URL will be made
                                            switch(deviceType){
                                                //If its light bulb
                                                case "Smart Light":
                                                    lightURls.add("https://" + IP + "/api/" + hueBridgeUsername + "/lights/" + numericID + "/state");
                                                    break;
                                                //If its a smart plug
                                                case "Smart Plug":
                                                    plugURLs.add("https://" + IP + "/api/" + hueBridgeUsername + "/lights/" + numericID + "/state");
                                                    break;
                                                case "Smart Device":
                                                    if(deviceName.contains("plug")){
                                                        plugURLs.add("https://" + IP + "/api/" + hueBridgeUsername + "/lights/" + numericID + "/state");
                                                    } else if (deviceName.contains("light") || deviceName.contains("lamp") ) {
                                                        lightURls.add("https://" + IP + "/api/" + hueBridgeUsername + "/lights/" + numericID + "/state");
                                                    }
                                                    break;

                                                default:
                                                    break;
                                            }
                                        }
                                    } else {
                                        // Handle case where numeric ID is not available
                                        Log.e("Error", "Numeric ID not found for device: " + deviceId);
                                    }
                                } else {
                                    // Handle case where device document does not exist
                                    Log.e("Error", "Device document not found for ID: " + deviceId);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure to retrieve device document
                                Log.e("Error", "Error retrieving device document: " + e.getMessage());
                            }
                        });
                    }
                } else {
                    // Handle case where no devices are associated with the user
                    Log.d("Info", "No devices found");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to retrieve user document
                Log.e("Error", "Error retrieving devices: " + e.getMessage());
            }
        });

        ImageButton but = (ImageButton)findViewById(R.id.ibHome2);
        Button btn = (Button)findViewById(R.id.button8);
        Button btn2 = (Button)findViewById(R.id.button12);
        Button btn3 = (Button)findViewById(R.id.button14);
        Button btn4 = (Button)findViewById(R.id.button13);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyGroup.this, homePage.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyGroup.this, MyDevicesActivity2.class));
            }
        });

       btn2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(MyGroup.this, "Living Room Turned On.",Toast.LENGTH_SHORT).show();
               for(String lightURL : lightURls){
                   turnOnDevice(lightURL);
               }
               //Go trough each plug to turn on
               for( String plugURL : plugURLs){
                   turnOnDevice(plugURL);
               }
                nestAPI.setHvacMode("COOL");
           }
       });

       btn3.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(MyGroup.this, "Living Room Turned Off.",Toast.LENGTH_SHORT).show();
               for(String lightURL : lightURls){
                   turnOffDevice(lightURL);
               }
               //Go trough each plug to turn on
               for( String plugURL : plugURLs){
                   turnOffDevice(plugURL);
               }
               nestAPI.setHvacMode("OFF");
           }
       });
    }

    private void sendRequest(final String urlString, final String requestBody) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                    outputStream.writeBytes(requestBody);
                    outputStream.flush();
                    outputStream.close();
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.d("HTTP Request", "Request sent successfully");
                    } else {
                        Log.e("HTTP Request", "Error sending request. Response code: " + responseCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("HTTP Request", "IOException: " + e.getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }
    private void turnOffDevice(String url) {
        configureSSL();
        String requestBody = "{\"on\": false}";
        sendRequest(url,requestBody);
    }

    private void turnOnDevice(String url) {
        configureSSL();
        String requestBody = "{\"on\": true}";
        sendRequest(url,requestBody);
    }

    private void configureSSL() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }

                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}