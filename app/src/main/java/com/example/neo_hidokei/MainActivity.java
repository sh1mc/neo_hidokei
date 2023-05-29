package com.example.neo_hidokei;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button hidokeiActivityButton = (Button) findViewById(R.id.button_calc);
        hidokeiActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Hidokei.class);
                startActivity(intent);
            }
        });

        Button invHidokeiActivityButton = (Button) findViewById(R.id.button_inv_hidokei);
        invHidokeiActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InvHidokei.class);
                startActivity(intent);
            }
        });
    }
}