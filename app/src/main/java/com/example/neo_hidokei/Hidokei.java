package com.example.neo_hidokei;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Hidokei extends AppCompatActivity implements SensorEventListener {
    float[] mAccelerationValue = new float[3];
    float[] mGeomagneticValue = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidokei);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        Button buttonCalc = (Button) findViewById(R.id.button_calc);
        buttonCalc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                float[] orientationValue = new float[3];
                float[] inRotationMatrix = new float[9];
                float[] outRotationMatrix = new float[9];
                float[] inclinationMatrix = new float[9];
                SensorManager.getRotationMatrix(inRotationMatrix, inclinationMatrix, mAccelerationValue, mGeomagneticValue);
                SensorManager.remapCoordinateSystem(inRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, outRotationMatrix);
                SensorManager.getOrientation(outRotationMatrix, orientationValue);
                TextView timeText = (TextView) findViewById(R.id.time);
                double degree = Math.toDegrees(orientationValue[0]);
                boolean is_am = degree > 0;
                double hour = is_am ? degree / 180 * 12 : (degree + 180) / 180 * 12;
                double min = hour % 1 * 60;
                timeText.setText(String.format("%s %02d:%02d", is_am ? "AM" : "PM", (int)Math.floor(hour), (int)Math.floor(min)));
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                lowpass(mGeomagneticValue, sensorEvent.values.clone());

                break;
            case Sensor.TYPE_ACCELEROMETER:
                lowpass(mAccelerationValue, sensorEvent.values.clone());
                break;
        }
    }

    private void lowpass(float[] val0, float[] val1) {
        final float alpha = 0.3f;
        assert(val0.length == val1.length);
        for (int i = 0; i < val0.length; i++) {
            val0[i] = alpha * val0[i] + (1.0f - alpha) * val1[i];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}