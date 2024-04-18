package com.example.smarthomie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
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
                if (documentSnapshot.exists()) {
                    // Get the list of devices from the document
                    List<String> listOfDevices = (List<String>) documentSnapshot.get("listOfDevices");
                    if (listOfDevices != null && !listOfDevices.isEmpty()) {

                        // Retrieve IP and hueBridgeUsername from Firestore for the first device
                        String firstDeviceName = listOfDevices.get(0);
                        retrieveDeviceInfo(firstDeviceName);
                    } else {
                        Toast.makeText(Scenarios.this, "No devices found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Scenarios.this, "User document does not exist", Toast.LENGTH_SHORT).show();
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
    //Method to change brightness relating to time
    // Method to simulate a sunrise wake-up routine
    private void increaseBrightness(String url, int durationInSeconds) {
        configureSSL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Get Brightness integer
        int numSteps = durationInSeconds;
        int initialBrightness = 1; // Starting brightness
        int finalBrightness = 254; // Maximum brightness
        int brightnessIncrement = (finalBrightness - initialBrightness) / numSteps;

        // Timer to increase brightness gradually
        for (int i = 0; i < numSteps; i++) {
            final int brightness = initialBrightness + i * brightnessIncrement;

            //Sending request
            String requestBody = "{\"on\": true, \"bri\": " + brightness + "}";
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Increasing Brightness", "Response from server: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Increasing Brightness", "Error increasing brightness: " + error.getMessage());
                }
            }) {
                @Override
                public byte[] getBody() {
                    return requestBody.getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            // Delaying request
            int delayInMillis = i * 1000; // 1000 milliseconds = 1 second
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestQueue.add(stringRequest);
                }
            }, delayInMillis);
        }
    }

    //Method to dim light for ECO Mode
    private void ecoMode(String url){
        configureSSL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String requestBody = "{\"on\": true, \"bri\": " + ecoBrightness + "}";
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LightControl", "Response from server: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
            }
        }) {
            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    // Method to turn off Devices
    private void turnOffDevice(String url) {
        configureSSL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Sending request to turn off light
        String requestBody = "{\"on\": false}";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LightControl", "Response from server: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
            }
        }) {
            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    // Method to turn on devices
    private void turnOnDevice(String url) {
        configureSSL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Sending request to turn on light
        String requestBody = "{\"on\": true}";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LightControl", "Response from server: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
            }
        }) {
            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
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

