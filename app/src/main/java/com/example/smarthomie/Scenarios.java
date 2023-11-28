package com.example.smarthomie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class Scenarios extends AppCompatActivity {
    ImageButton myImageButton;
    private String bridgeIpAddress;
    private String username;
     Button offLight;

    protected void discoverBridges() {
        String url = "https://discovery.meethue.com/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject bridge = response.getJSONObject(0); // Assuming there's at least one bridge
                                String internalIpAddress = bridge.getString("internalipaddress");
                                bridgeIpAddress = internalIpAddress;
                                // Show the IP Address in a Toast message
                                Toast.makeText(Scenarios.this, "Bridge IP: " + bridgeIpAddress, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Scenarios.this, "No bridges found", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Scenarios.this, "Error parsing the response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Scenarios.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        MySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void turnOffLight(String url) {
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


        RequestQueue requestQueue = Volley.newRequestQueue(this);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenarios_page);

        myImageButton = (ImageButton) findViewById(R.id.ibHome);

        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(Scenarios.this, homePage.class);
                startActivity(goToHomePage);
            }

        });
        Button button = findViewById(R.id.btnAwayScenario);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (bridgeIpAddress != null && !bridgeIpAddress.isEmpty()) {
                    String url = "https://" + bridgeIpAddress + "/api/lV3d2XWI7xCZ6tbqpMvFjczsEwfE6arYnF7Ca2Nl/lights/1/state";
                    turnOffLight(url);
                } else {
                    Toast.makeText(Scenarios.this, "Please discover the bridge first", Toast.LENGTH_LONG).show();
                }
            }
            });
        offLight = findViewById(R.id.btnHomeScenario);
        offLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverBridges();
            }
        });
    }

}