package com.delta.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static String URL = "http://api.openweathermap.org/data/2.5/";
    private static String API_KEY="7adf93605d2758b57f8a318084e5909f";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}