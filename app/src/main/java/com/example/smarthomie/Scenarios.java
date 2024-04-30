package com.example.smarthomie;

import android.content.Context;
import  android.content.Intent;

import android.os.Bundle;

import android.os.Looper;
import android.os.Message;
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
    ImageButton settingsImageButton;

    private String numericID;

    String userId;
    FirebaseAuth auth;
    FirebaseFirestore db;
    int ecoBrightness = 50;
    private List<String> lightURls = new ArrayList<>();
    private List<String> plugURLs = new ArrayList<>();
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
                                            switch (deviceType) {
                                                //If its light bulb
                                                case "Smart Light":
                                                    lightURls.add("https://" + IP + "/api/" + hueBridgeUsername + "/lights/" + numericID + "/state");
                                                    break;
                                                //If its a smart plug
                                                case "Smart Plug":
                                                    plugURLs.add("https://" + IP + "/api/" + hueBridgeUsername + "/lights/" + numericID + "/state");
                                                    break;
                                                case "Smart Device":
                                                    if (deviceName.contains("plug")) {
                                                        plugURLs.add("https://" + IP + "/api/" + hueBridgeUsername + "/lights/" + numericID + "/state");
                                                    } else if (deviceName.contains("light") || deviceName.contains("lamp")) {
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
        //Settings Page
        settingsImageButton = findViewById(R.id.ibSettings);
        settingsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(Scenarios.this, ScenarioSettingsActivity.class);
                startActivity(goToSettings);
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
                //Go through each Light in list to turn on
                for (String lightURL : lightURls) {
                    turnOnDevice(lightURL);
                }
                //Go trough each plug to turn on
                for (String plugURL : plugURLs) {
                    turnOnDevice(plugURL);
                }
                nestAPI.turnOnNestDevice();
                Toast.makeText(Scenarios.this, "Home Scenario Enabled", Toast.LENGTH_SHORT).show();
            }
        });

        //Away Scenario
        Button awayScenarioButton = findViewById(R.id.btnAwayScenario);
        awayScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iterate through lights to turn off
                for (String lightURL : lightURls) {
                    turnOffDevice(lightURL);
                }
                //iterate through plugs to turn off
                for (String plugURl : plugURLs) {
                    turnOffDevice(plugURl);
                }
                nestAPI.setHvacMode("OFF");
                Toast.makeText(Scenarios.this, "Away Scenario Enabled", Toast.LENGTH_SHORT).show();
            }
        });
        //Eco Scenario
        Button ecoScenarioButton = findViewById(R.id.btnEcoScenario);
        ecoScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean anyLightOn = false;
                boolean anyPlugOn = false;
                //iterate through lights to set to eco mode
                for (String lightURL : lightURls) {
                    ecoMode(lightURL);
                }
                //iterate through plugs to set to eco mode
                for (String plugURl : plugURLs) {
                    ecoMode(plugURl);
                }
                //set Nest into eco mode
                nestAPI.setHvacMode("MANUAL_ECO");
                Toast.makeText(Scenarios.this, "Eco Scenario Enabled", Toast.LENGTH_SHORT).show();
            }
        });
        // Sleep Scenario
        Button sleepButton = findViewById(R.id.btnSleepScenario);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iterate through lights to gradually turn off
                for (String lightURL : lightURls) {
                    decreaseBrightness(lightURL);
                }
                // Iterate through plugs to turn off
                for (String plugURl : plugURLs) {
                    turnOffDevice(plugURl);
                }
                // Set HVAC mode for Sleep Scenario
                nestAPI.setHvacMode(getHvacModeSleep());
                // Show toast message indicating Sleep Scenario is enabled
                Toast.makeText(Scenarios.this, "Sleep Scenario Activated: " + getHvacModeSleep() + " Mode " + getSelectedDurationSleep() + "sec", Toast.LENGTH_SHORT).show();
            }
        });

        // Wake Up Scenario
        Button wakeUpButton = findViewById(R.id.btnCustomScenario);
        wakeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iterate through lights to gradually increase brightness
                for (String lightURl : lightURls) {
                    increaseBrightness(lightURl);
                }
                // Iterate through plugs to turn on
                for (String plugURL : plugURLs) {
                    turnOnDevice(plugURL);
                }
                // Set HVAC mode for Wake Up Scenario
                nestAPI.setHvacMode(getHvacModeWakeUp());
                // Show toast message indicating Wake Up Scenario is activated
                Toast.makeText(Scenarios.this, "Wake up Scenario Activated: " + getHvacModeWakeUp() + " Mode " + getSelectedDurationWakeUp() + "sec" , Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Method to retrieve cycle duration for Sleep Scenario
    private int getSelectedDurationSleep() {
        return new ScenarioSettingsActivity().getSelectedDuration(this, "sleepDurationKey");
    }

    //Method to retrieve cycle duration for Wake Up Scenario
    private int getSelectedDurationWakeUp() {
        return new ScenarioSettingsActivity().getSelectedDuration(this, "wakeUpDurationKey");
    }

    //Method to retrieve HVAC mode for Sleep Scenario
    private String getHvacModeSleep() {
        return new ScenarioSettingsActivity().getSelectedMode(this, "sleepModeKey");
    }

    //Method to retrieve HVAC mode for Wake Up Scenario
    private String getHvacModeWakeUp() {
        return new ScenarioSettingsActivity().getSelectedMode(this, "wakeUpModeKey");
    }


    //Gradually decrease brightness for Sleep Scenario
    private void decreaseBrightness(final String url) {
        configureSSL();
        final int durationInSeconds = getSelectedDurationSleep();
        final int totalDurationMillis = durationInSeconds * 1000; // Convert duration to milliseconds
        final int initialBrightness = 254; // Maximum
        final int finalBrightness = 0; // Minimum

        // Calculate brightness decrement per millisecond to achieve final brightness in the selected duration
        final double brightnessDecrementPerMillis = (double) (initialBrightness - finalBrightness) / totalDurationMillis;

        // Request delay in separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                int brightness = initialBrightness;
                long startTime = System.currentTimeMillis();
                long previousBrightnessUpdate = startTime;
                while (brightness > finalBrightness) {
                    // Calculate elapsed time
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - startTime;

                    // Calculate expected brightness based on elapsed time
                    int newBrightness = initialBrightness - (int) (brightnessDecrementPerMillis * elapsedTime);

                    // Ensure brightness does not go below the final brightness
                    if (newBrightness < finalBrightness) {
                        newBrightness = finalBrightness;
                    }

                    // Update brightness only if it has changed since the last update
                    if (newBrightness != brightness) {
                        brightness = newBrightness;

                        // Sending delayed request
                        String requestBody = "{\"on\": true, \"bri\": " + brightness + "}";
                        sendRequest(url, requestBody);

                        // Update the time of the last brightness update
                        previousBrightnessUpdate = currentTime;
                    }

                    // Delay for a short interval (e.g., 100 milliseconds) between brightness adjustments
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Turn off the light after the selected duration
                String offRequest = "{\"on\": false}";
                sendRequest(url, offRequest);
            }
        }).start();
    }
    // Gradually Increase brightness For wake up Scenario
    private void increaseBrightness(final String url) {
        configureSSL();
        // Get Brightness integer
        final int numSteps = getSelectedDurationWakeUp();
        final int initialBrightness = 1; // Starting brightness
        final int finalBrightness = 254; // Maximum brightness
        final int brightnessIncrement = (finalBrightness - initialBrightness) / numSteps;

        // Create a separate thread to send requests with delay
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < numSteps; i++) {
                    final int brightness = initialBrightness + i * brightnessIncrement;

                    //Delay request in order to gradually increase brightness
                    String requestBody = "{\"on\": true, \"bri\": " + brightness + "}";
                    sendRequestWithDelay(url, requestBody, i * 1000); // Delay request by i seconds
                }
            }
        }).start();
    }

    // Send HTTP request with delay
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
     //Method To tunr off device
    private void turnOffDevice(String url) {
        configureSSL();
        String requestBody = "{\"on\": false}";
        sendRequest(url,requestBody);
    }

    // Method to turn on devices
    private void turnOnDevice(String url) {
        configureSSL();
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

