package com.example.smarthomie;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class NestAPI {
    private static String oauth2ClientId = "418693695368-54td1or6bqlqiarsd17po53005ejku4h.apps.googleusercontent.com";
    private static String clientSecret = "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";
    private static String projectId = "13158699-1f9d-4843-868c-73c644a96733";
    private static String authorizationCode = "ya29.a0AfB_byBKlH1WuZ54dKM4hOf3OvRUV-_42KCtUKn1teJPklsvrmkAOFXwUYvxAbfoJximo-mvG8Rc3desGwnePK8MQKcOlnXzkQkqksbTNVw7A11YKS7EDysGyhibXdZ-5H2sSauuIjgJSaSm5jvd8xPl7e84DGcFj96GaCgYKARoSARMSFQHGX2MiJj5n_LqluxnQ7lyAPqBjvg0171";
    private static String accessToken = "";
    private static String refreshToken = "1//06_SnvSzckj_ZCgYIARAAGAYSNwF-L9Irx3txViEDkM5ni4EtfrPA307g0L4wm1gzXDgT-CQ_Q58WfEE3ZuI710QmYmUtu84vS7U";
    private static String deviceId = "AVPHwEsGqDW61332djQHyxAmoqAlJZopE7z2e4UaQtrZc71Bv3WtsY-ICKGDoMptaOfxGVPIbQe3oJxNN5dujhWm6gNIvQ";

    private String lastMode = ""; //Keep track of the last Mode it was on before turning off

    public static void main(String[] args) {

        refreshToken();
        // Example actions


        // Uncomment the following line if you want to pause and check whether the temperature setpoint changed.
        // System.in.read();

        temperatureSetCool(81);


    }

    private static void requestTokens() {
        try {
            // Load tokens from file if available
            File file = new File(".mynest.json");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    StringBuilder data = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        data.append(line);
                    }

                    String jsonContent = data.toString();
                    // Parse JSON to get access and refresh tokens
                    accessToken = jsonContent.split("\"access_token\":\"")[1].split("\"")[0];
                    refreshToken = jsonContent.split("\"refresh_token\":\"")[1].split("\"")[0];

                    System.out.println("Access token: " + accessToken);
                    System.out.println("Refresh token: " + refreshToken);
                }
            } else {
                // Request tokens if not available
                String requestTokenUrl = "https://www.googleapis.com/oauth2/v4/token";
                String params = "client_id=" + oauth2ClientId +
                        "&client_secret=" + clientSecret +
                        "&code=" + authorizationCode +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=https://www.google.com";

                HttpURLConnection requestTokenConnection = (HttpURLConnection) new URL(requestTokenUrl).openConnection();
                requestTokenConnection.setRequestMethod("POST");
                requestTokenConnection.setDoOutput(true);

                try (OutputStream outputStream = requestTokenConnection.getOutputStream()) {
                    outputStream.write(params.getBytes(StandardCharsets.UTF_8));
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestTokenConnection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse JSON response to get access and refresh tokens
                    String jsonResponse = response.toString();
                    accessToken = jsonResponse.split("\"access_token\":\"")[1].split("\"")[0];
                    refreshToken = jsonResponse.split("\"refresh_token\":\"")[1].split("\"")[0];

                    System.out.println("Access token: " + accessToken);
                    System.out.println("Refresh token: " + refreshToken);

                    // Save tokens to file
                    try (FileWriter fileWriter = new FileWriter(file)) {
                        fileWriter.write(jsonResponse);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void refreshToken() {
        try {
            String refreshUrl = "https://www.googleapis.com/oauth2/v4/token?";
            String params = "client_id=" + oauth2ClientId +
                    "&client_secret=" + clientSecret +
                    "&refresh_token=" + refreshToken +
                    "&grant_type=refresh_token";

            HttpURLConnection refreshConnection = (HttpURLConnection) new URL(refreshUrl).openConnection();
            refreshConnection.setRequestMethod("POST");
            refreshConnection.setDoOutput(true);

            try (OutputStream outputStream = refreshConnection.getOutputStream()) {
                outputStream.write((params).getBytes(StandardCharsets.UTF_8));
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(refreshConnection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parse JSON response to get new access token
                accessToken = response.toString().split("\"access_token\":\"")[0].split("\"")[3];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setHvacMode(String mode) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://smartdevicemanagement.googleapis.com/v1/enterprises/" + projectId + "/devices/" + deviceId + ":executeCommand";
                    URL urlo = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlo.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                    String data = "{\"command\":\"sdm.devices.commands.ThermostatMode.SetMode\",\"params\":{\"mode\":\"" + mode + "\"}}";

                    try (OutputStream outputStream = connection.getOutputStream()) {
                        byte[] input = data.getBytes("utf-8");
                        outputStream.write(input, 0, input.length);
                    }

                    System.out.println("execute_response: " + connection.getResponseCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
//        try {
//            String url = "https://smartdevicemanagement.googleapis.com/v1/enterprises/" + projectId + "/devices/" + deviceId + ":executeCommand";
//            URL urlo = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) urlo.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
//
//            String data = "{\"command\":\"sdm.devices.commands.ThermostatMode.SetMode\",\"params\":{\"mode\":\"" + mode + "\"}}";
//
//            try (OutputStream outputStream = connection.getOutputStream()) {
//                byte[] input = data.getBytes("utf-8");
//                outputStream.write(input, 0, input.length);
//            }
//
//            System.out.println("execute_response: " + connection.getResponseCode());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static void temperatureSetHeat(double setpoint) {
        try {
            String url = "https://smartdevicemanagement.googleapis.com/v1/enterprises/" + projectId + "/devices/" + deviceId + ":executeCommand";
            URL urlo = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlo.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setDoOutput(true);

            String data = "{\"command\":\"sdm.devices.commands.ThermostatTemperatureSetpoint.SetHeat\",\"params\":{\"heatCelsius\":" + (setpoint - 32) / 9 * 5 + "}}";
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = data.getBytes("UTF-8");
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("execute_response: " + connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void temperatureSetCool(double setpoint) {
        try {
            String url = "https://smartdevicemanagement.googleapis.com/v1/enterprises/" + projectId + "/devices/" + deviceId + ":executeCommand";
            URL urlo = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlo.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setDoOutput(true);

            String data = "{\"command\":\"sdm.devices.commands.ThermostatTemperatureSetpoint.SetCool\",\"params\":{\"coolCelsius\":" + (setpoint - 32) / 9 * 5 + "}}";
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = data.getBytes("UTF-8");
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("execute_response: " + connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to turn off the device
    public void turnOffNestDevice() {
        sendDeviceCommand("OFF");
    }
    //method to turn on Nest to last mode
    public void turnOnNestDevice(){
        if(!lastMode.isEmpty()){
            setHvacMode(lastMode);
        }else{
            setHvacMode("heat");//Default Mode
        }
    }

    // Private method to send a device command
    private static void sendDeviceCommand(String command) {
        try {
            String url = "https://smartdevicemanagement.googleapis.com/v1/enterprises/" + projectId + "/devices/" + deviceId + ":executeCommand";
            URL urlo = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlo.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            String data = "{\"command\":\"" + command + "\"}";

            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            System.out.println("execute_response: " + connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
