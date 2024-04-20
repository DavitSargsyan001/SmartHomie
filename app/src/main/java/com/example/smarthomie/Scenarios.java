package com.example.smarthomie;



import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class Scenarios extends AppCompatActivity {
    ImageButton myImageButton;
    private String bridgeIpAddress;
    private String username;
    int lightID = 1;
    int plugID = 3;
    String email, userId;
    FirebaseAuth auth;
    FirebaseFirestore db;
    private String lightURl;
    private String plugURL;

    int ecoBrightness = 50;

    NestAPI nestAPI = new NestAPI();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenarios_page);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get current user info
        userId = auth.getCurrentUser().getUid();

        // Retrieve devices associated with the user
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<?> listOfDevices = (List<?>) documentSnapshot.get("listOfDevices");
                if (listOfDevices != null && !listOfDevices.isEmpty()) {
                    List<String> deviceNames = new ArrayList<>();
                    for (Object device : listOfDevices) {
                        if (device instanceof String) {
                            deviceNames.add((String) device);
                        } else {
                            // Handle unexpected types if necessary
                            Toast.makeText(Scenarios.this, "No devices found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // Retrieve IP and hueBridgeUsername from Firestore for the first device
                    String firstDeviceName = deviceNames.get(0);
                    retrieveDeviceInfo(firstDeviceName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Scenarios.this, "Error retrieving devices: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Go back to home page
        myImageButton = findViewById(R.id.ibHome);
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(Scenarios.this, homePage.class);
                startActivity(goToHomePage);
            }
        });

        //Home Scenario
        Button homeScenarioButton = findViewById(R.id.btnHomeScenario);
        homeScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lightURl != null) {
                    turnOnDevice(lightURl);
                }
                if (plugURL != null) {
                    turnOnDevice(plugURL);
                }
                nestAPI.turnOnNestDevice();
            }
        });

        //Away Scenario
        Button awayScenarioButton = findViewById(R.id.btnAwayScenario);
        awayScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lightURl != null) {
                    turnOffDevice(lightURl);
                }
                if (plugURL != null) {
                    turnOffDevice(plugURL);
                }
                nestAPI.turnOffNestDevice();
            }
        });
        //Eco Scenario
        Button ecoScenarioButton = findViewById(R.id.btnEcoScenario);
        ecoScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lightURl != null) {
                    ecoMode(lightURl);
                }
                if (plugURL != null) {
                    turnOffDevice(plugURL);
                }
            }
        });
        //Sleep Scenario
        Button sleepButton = findViewById(R.id.btnSleepScenario);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lightURl != null){
                    decreaseBrightness(lightURl,30);
                }
                if(plugURL != null){
                    turnOffDevice(plugURL);
                }
            }
        });
        //Wake Up Scenario
        Button wakeUpButton = findViewById(R.id.btnCustomScenario);
        wakeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lightURl != null) {
                    increaseBrightness(lightURl,30);
                }
                if (plugURL != null) {
                    turnOnDevice(plugURL);
                }
            }
        });
    }

    // Get IP and Username from database
    private void retrieveDeviceInfo(String deviceName) {
        DocumentReference deviceRef = db.collection("Devices").document(deviceName);
        deviceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    bridgeIpAddress = documentSnapshot.getString("IP");
                    username = documentSnapshot.getString("hueBridgeUsername: ");
                    // LOGGING PURPOSES REMEMBER TO DELETE
                    String message = "IP: " + bridgeIpAddress + "\nHue Bridge Username: " + username;
                    Toast.makeText(Scenarios.this, message, Toast.LENGTH_SHORT).show();
                    //creating the urls to send request
                    lightURl = "https://" + bridgeIpAddress + "/api/" + username + "/lights/" + lightID + "/state";
                    plugURL = "https://" + bridgeIpAddress + "/api/" + username + "/lights/" + plugID + "/state";
                } else {
                    Toast.makeText(Scenarios.this, "Device document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Scenarios.this, "Error retrieving device info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Gradually decrease brightness for Sleep Scenario
    private void decreaseBrightness(final String url, final int durationInSeconds){
        configureSSL();
        final int numSteps = durationInSeconds;
        final int initialBrightness = 254;//Maximum
        final int finalBrightness = 1; //Minimum
        final int brightnessIncrement = (initialBrightness - finalBrightness) / numSteps;

        //Request delay in separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < numSteps; i++){
                    final int brightness = initialBrightness - i * brightnessIncrement;

                    //Sending delayed request
                    String requestBody = "{\"on\": true, \"bri\": " + brightness + "}";
                    sendRequestWithDelay(url, requestBody, i * 1000); // Delay request by i seconds
                }
            }
        }).start();
    }

    // Gradually Increase brightness For wake up Scenario
    private void increaseBrightness(final String url, final int durationInSeconds) {
        configureSSL();
        // Get Brightness integer
        final int numSteps = durationInSeconds;
        final int initialBrightness = 1; // Starting brightness
        final int finalBrightness = 254; // Maximum brightness
        final int brightnessIncrement = (finalBrightness - initialBrightness) / numSteps;

        // Create a separate thread to send requests with delay
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < numSteps; i++) {
                    final int brightness = initialBrightness + i * brightnessIncrement;

                    //Sending request with delay
                    String requestBody = "{\"on\": true, \"bri\": " + brightness + "}";
                    sendRequestWithDelay(url, requestBody, i * 1000); // Delay request by i seconds
                }
            }
        }).start();
    }

    // Method to send HTTP request with delay
    private void sendRequestWithDelay(final String url, final String requestBody, final int delayInMillis) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendRequest(url, requestBody);
                    }
                }, delayInMillis);
            }
        });
    }


    // Method to send HTTP request
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

    //Method to dim light for ECO Mode
    private void ecoMode(String url){
            configureSSL();
            // Sending request to set eco mode
            String requestBody = "{\"on\": true, \"bri\": " + ecoBrightness + "}";
            sendRequest(url, requestBody);
        }
        //Method to turn off devices
    private void turnOffDevice(String url) {
        configureSSL();
        // Sending request to turn off light
        String requestBody = "{\"on\": false}";
        sendRequest(url,requestBody);
    }

    // Method to turn on devices
    private void turnOnDevice(String url) {
        configureSSL();
        // Sending request to turn on light
        String requestBody = "{\"on\": true}";
        sendRequest(url,requestBody);
    }
    // SSL handshake error fix
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

