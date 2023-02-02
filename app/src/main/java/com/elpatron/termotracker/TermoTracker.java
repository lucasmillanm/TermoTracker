package com.elpatron.termotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TermoTracker extends AppCompatActivity implements SensorEventListener {

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notification", "channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        this.setTextDisplayed(sensorEvent.values[0] + " ℃");
        NotificationCompat.Builder notification = new NotificationCompat.Builder(TermoTracker.this, "notification");
        if (sensorEvent.values[0] >= 30) {
            this.setColor("#DC583C");
            this.handleNotification(notification, "It’s already more than 30 degrees outside, wear shorts");
        } else if (sensorEvent.values[0] <= 0) {
            this.setColor("#3C9BDC");
            this.handleNotification(notification, "It’s below 0 degrees, it’s freezing, wear something warm");
        } else {
            textDisplayed.setTextColor(Color.GRAY);
        }
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

    private void setColor(String color) {
        textDisplayed.setTextColor(Color.parseColor(color));
    }

    private void handleNotification(NotificationCompat.Builder notification, String contentText) {
        notification.setContentTitle("TempoTracker");
        notification.setContentText(contentText);
        notification.setSmallIcon(R.drawable.ic_launcher_foreground);
        notification.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(TermoTracker.this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(0, notification.build());
    }
}