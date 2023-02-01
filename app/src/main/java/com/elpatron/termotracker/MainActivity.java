package com.elpatron.termotracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textDisplayed;
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private boolean isTemperatureSensorAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textDisplayed = findViewById(R.id.temperature);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTemperatureSensorAvailable = true;
        } else {
            this.setTextDisplayed("Temperature not available");
            isTemperatureSensorAvailable = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        this.setTextDisplayed(sensorEvent.values[0] + " ℃");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterListener();
    }

    private void setTextDisplayed(String text) {
        this.textDisplayed.setText(text);
    }

    private void registerListener() {
        if (isTemperatureSensorAvailable) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    private void unregisterListener() {
        if (isTemperatureSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
    }
}