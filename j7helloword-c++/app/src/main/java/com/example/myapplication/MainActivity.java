package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tvw1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        TextView tvw2 = findViewById(R.id.tvw2);
        tvw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = Test.getString(5);
                tvw2.setText(string);
            }
        });

        TextView tvw3 = findViewById(R.id.tvw3);
        tvw3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = Hello.getNative(5);
                tvw3.setText(string);
            }
        });
    }
}