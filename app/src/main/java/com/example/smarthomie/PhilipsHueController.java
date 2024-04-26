package com.example.smarthomie;
import androidx.appcompat.app.AppCompatActivity;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class PhilipsHueController extends AppCompatActivity {

    public static void main(String[] args) {
        String bridgeIP = "YOUR_BRIDGE_IP";

        // Replace with your API key
        String apiKey = "YOUR_API_KEY";

        // The ID of the light you want to control
        String lightId = "1";

        // Base URL for Philips Hue API
        String baseUrl = "https://" + bridgeIP + "/api/" + apiKey;

        // Create an SSLContext that trusts all certificates (for testing purposes)
        SSLContext sslContext = createTrustAllSSLContext();

        // Turn the light on
        turnOnLight(sslContext, lightId);

        // Change the brightness of the light
        setBrightness(lightId, "127", sslContext);
    }

    public static void turnOnLight(SSLContext sslContext, String light) {
        try {
            String endpoint = "https://192.168.68.63/api/RRL9E9N5KzHKbUiZ-FYXz--tUxmHUaW8mByLQREa/lights/" + light + "/state";
            URL url = new URL(endpoint);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set the SSL context to trust all certificates
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String requestBody = "{\"on\":true}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                System.out.println("Light turned on successfully.");
            } else {
                System.err.println("Failed to turn on the light. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void turnOffLight(SSLContext sslContext, String light) {
        try {
            String endpoint = "https://192.168.68.63/api/RRL9E9N5KzHKbUiZ-FYXz--tUxmHUaW8mByLQREa/lights/" + light + "/state";
            URL url = new URL(endpoint);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set the SSL context to trust all certificates
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String requestBody = "{\"on\":false}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                System.out.println("Light turned on successfully.");
            } else {
                System.err.println("Failed to turn on the light. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setBrightness(String light, String brightness, SSLContext sslContext) {
        try {
            String endpoint = "https://192.168.68.63/api/RRL9E9N5KzHKbUiZ-FYXz--tUxmHUaW8mByLQREa/lights/" + light + "/state";
            URL url = new URL(endpoint);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set the SSL context to trust all certificates
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String requestBody = "{\"bri\":" + brightness + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                System.out.println("Brightness set successfully.");
            } else {
                System.err.println("Failed to set brightness. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create an SSLContext that trusts all certificates (for testing purposes)
    public static SSLContext createTrustAllSSLContext() {
        try {
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Set the hostname verifier on the SSL context
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

