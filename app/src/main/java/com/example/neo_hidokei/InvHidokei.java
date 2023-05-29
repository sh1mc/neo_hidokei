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

import java.util.Calendar;

public class InvHidokei extends AppCompatActivity implements SensorEventListener {
    float[] mAccelerationValue = new float[3];
    float[] mGeomagneticValue = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_hidokei);
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
                TextView arrowText = (TextView) findViewById(R.id.arrow);
                double degree = -Math.toDegrees(orientationValue[0]);
                Calendar calendar = Calendar.getInstance();
                boolean is_am = calendar.get(Calendar.AM_PM) == Calendar.AM;
                double hour = (double)calendar.get(Calendar.HOUR) + (double)calendar.get(Calendar.MINUTE) / 60;
                double arrow_degree = is_am ? degree + hour / 12 * 180 : degree - ((1 - hour / 12) * 180) ;
                arrowText.setText("↑");
                arrowText.setRotation((float)arrow_degree);
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