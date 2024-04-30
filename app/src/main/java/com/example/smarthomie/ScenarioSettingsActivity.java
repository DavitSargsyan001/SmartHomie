package com.example.smarthomie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScenarioSettingsActivity extends AppCompatActivity {
    ImageButton homeButton;
    Integer[] durationInSeconds = {null, 30, 300, 900, 1800}; // Durations in seconds
    String[] sleepHvacMode = {" ", "HEAT", "COOL"};
    boolean hvacModeItemSelected = false;
    boolean wakeUpItemSelected = false;
    boolean durationItemSelected = false;
    boolean wakeUpHvacSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenarios_settings);

        homeButton = findViewById(R.id.ibHome);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHomePage = new Intent(ScenarioSettingsActivity.this, Scenarios.class);
                startActivity(goToHomePage);
            }
        });

        // Convert durations from seconds to minutes for display
        String[] durationOptions = new String[durationInSeconds.length];
        for (int i = 0; i < durationInSeconds.length; i++) {
            if (durationInSeconds[i] == null) {
                durationOptions[i] = ""; // Empty string for the first option
            } else if (durationInSeconds[i] == 30) {
                durationOptions[i] = "30 secs";
            } else {
                durationOptions[i] = convertToMinutes(durationInSeconds[i]);
            }
        }

        // Get selection from user for Wake Up Scenario HVAC Mode
        Spinner hvacModeWakeUpSpinner = findViewById(R.id.spWakeUpHvacMode);
        ArrayAdapter<String> spWakeupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sleepHvacMode);
        spWakeupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hvacModeWakeUpSpinner.setAdapter(spWakeupAdapter);

        hvacModeWakeUpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (wakeUpHvacSelected) {
                    String selectedWakeUpMode = sleepHvacMode[position];
                    saveHvacModeSelection(selectedWakeUpMode, "wakeUpModeKey");
                    Toast.makeText(ScenarioSettingsActivity.this, "HVAC Mode Set: " + selectedWakeUpMode, Toast.LENGTH_SHORT).show();
                } else {
                    wakeUpHvacSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Get selection from user for Sleep Scenario HVAC Mode
        Spinner hvacModeSleepSpinner = findViewById(R.id.spHvacMode);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sleepHvacMode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hvacModeSleepSpinner.setAdapter(adapter);

        hvacModeSleepSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (hvacModeItemSelected) {
                    String selectedSleepMode = sleepHvacMode[position];
                    saveHvacModeSelection(selectedSleepMode, "sleepModeKey");
                    Toast.makeText(ScenarioSettingsActivity.this, "HVAC MODE Set: " + selectedSleepMode, Toast.LENGTH_SHORT).show();
                } else {
                    hvacModeItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        // Get selection for Wake up Cycle Duration
        Spinner wakeUpSpinner = findViewById(R.id.spDurationWakeUp);
        ArrayAdapter<String> wakeUpAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        wakeUpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wakeUpSpinner.setAdapter(wakeUpAdapter);
        wakeUpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (wakeUpItemSelected) {
                    int selectedDuration = durationInSeconds[position];
                    saveSelectedDuration(selectedDuration, "wakeUpDurationKey");
                    if (position == 1) {
                        Toast.makeText(ScenarioSettingsActivity.this, "Selected Duration: " + selectedDuration + "sec", Toast.LENGTH_SHORT).show();
                        return; // Exit the method to prevent execution of the subsequent Toast
                    }
                    Toast.makeText(ScenarioSettingsActivity.this, "Selected Duration: " + convertToMinutes(selectedDuration), Toast.LENGTH_SHORT).show();
                } else {
                    wakeUpItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        // Duration from user for Sleep scenario
        Spinner durationSpinner = findViewById(R.id.spDuration);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(adapter2);

        // Set listener for spinner selection
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Check if an item is selected for the first time
                if (durationItemSelected) {
                    // Save the selected duration option
                    int selectedDurationInSeconds = durationInSeconds[position];
                    saveSelectedDuration(selectedDurationInSeconds, "sleepDurationKey");
                    if (position == 1) {
                        Toast.makeText(ScenarioSettingsActivity.this, "Selected Duration: " + selectedDurationInSeconds + " sec", Toast.LENGTH_SHORT).show();
                        return; // Exit the method to prevent execution of the subsequent Toast
                    }
                    Toast.makeText(ScenarioSettingsActivity.this, "Selected Duration: " + convertToMinutes(selectedDurationInSeconds), Toast.LENGTH_SHORT).show();
                } else {
                    // Set the flag to true to indicate that an item has been selected
                    durationItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });
    }

    // Convert seconds to minutes for display
    private String convertToMinutes(int seconds) {
        int minutes = seconds / 60;
        return minutes + " mins";
    }

    // Save HVAC MODE
    private void saveHvacModeSelection(String mode, String key) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, mode);
        editor.apply();
    }

    // Get selected HVAC MODE
    public String getSelectedMode(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return preferences.getString(key, "Null");
    }

    // Save the selected duration option
    private void saveSelectedDuration(int durationInSeconds, String key) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, durationInSeconds);
        editor.apply();
    }

    // Get selected duration
    public int getSelectedDuration(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }
}
