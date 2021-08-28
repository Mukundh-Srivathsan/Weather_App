package com.delta.hackathon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.delta.hackathon.models.FinalData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "7adf93605d2758b57f8a318084e5909f";
    private static final String IMG_URL = "http://openweathermap.org/img/w/";

    private FinalData data;

    private JSONWeatherAPI jsonWeatherAPI;

    private TextView description;
    private TextView wind;
    private TextView humidity;
    private TextView pressure;
    private TextView temp;
    private TextView minMax;
    private TextView location;
    private TextView latLon;

    FusedLocationProviderClient locationClient;

    private double lat, lon;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Started");

        description = findViewById(R.id.description);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.wind);
        pressure = findViewById(R.id.pressure);
        temp = findViewById(R.id.temperature);
        minMax = findViewById(R.id.minMax);
        location = findViewById(R.id.location);
        latLon = findViewById(R.id.latLon);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonWeatherAPI = retrofit.create(JSONWeatherAPI.class);

        getLocation();


    }

    private void getData() {

        Log.d(TAG, "getData: Called");

        Call<FinalData> call = jsonWeatherAPI.getUsingCoord(lat, lon, API_KEY);

        call.enqueue(new Callback<FinalData>() {
            @Override
            public void onResponse(Call<FinalData> call, Response<FinalData> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Unsuccessful" + response.message());
                    Toast.makeText(MainActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    return;
                }

                data = response.body();

                String str = data.getWeather().get(0).getDesc();

                str = str.substring(0, 1).toUpperCase() + str.substring(1);

                description.setText(str);

                wind.setText(data.getWind().getSpeed());
                humidity.setText(data.getMain().getHumidity());
                pressure.setText(data.getMain().getPressure());

                temp.setText(data.getMain().getTemp());
                minMax.setText(data.getMain().getMin() + " / " + data.getMain().getMax());

                location.setText(data.getName());

                latLon.setText(data.getCoord().getLat() + " / " + data.getCoord().getLon());
            }

            @Override
            public void onFailure(Call<FinalData> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed" + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void getLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "Requesting Permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2000);
        } else {
            locationClient.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {

                    locationClient.removeLocationUpdates(this);

                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        lat = locationResult.getLastLocation().getLatitude();

                        Log.d(TAG, "lat: " + lat);
                        
                        lon = locationResult.getLastLocation().getLongitude();

                        Log.d(TAG, "lon: " + lon);

                        getData();
                    }

                    super.onLocationResult(locationResult);
                }
            }, Looper.getMainLooper());
        }
    }
}