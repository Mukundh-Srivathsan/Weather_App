package com.delta.hackathon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.delta.hackathon.models.FinalData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    private static final String URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "7adf93605d2758b57f8a318084e5909f";
    private static final String IMG_URL = "http://openweathermap.org/img/w/";
    private static final String METRIC = "metric";
    private static final int ERROR_DIALOG = 9001;

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
    private FloatingActionButton speech;

    private TextToSpeech textToSpeech;

    private FusedLocationProviderClient locationClient;

    private double lat, lon;

    private String speechText;

    private GoogleMap maps;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Started");

        description = findViewById(R.id.description);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        temp = findViewById(R.id.temperature);
        minMax = findViewById(R.id.minMax);
        location = findViewById(R.id.location);
        latLon = findViewById(R.id.latLon);
        speech = findViewById(R.id.speech);

        speech.setEnabled(false);

        speechText = "";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonWeatherAPI = retrofit.create(JSONWeatherAPI.class);

        checkServices();

//        getLocation();


    }

    private void getData() {

        Log.d(TAG, "getData: Called");

        Call<FinalData> call = jsonWeatherAPI.getUsingCoord(lat, lon, API_KEY, METRIC);

        call.enqueue(new Callback<FinalData>() {
            @Override
            public void onResponse(Call<FinalData> call, Response<FinalData> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Unsuccessful" + response.message());
                    Toast.makeText(MainActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    return;
                }

                data = response.body();

                setData();
            }

            @Override
            public void onFailure(Call<FinalData> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed" + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void setData() {

        String str = data.getWeather().get(0).getDesc();
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        description.setText(str);

        speechText += "Currently " + str;

        temp.setText(data.getMain().getTemp() + "°C");

        speechText += ". Temperature " + data.getMain().getTemp() + "°C";

        str = data.getWind().getSpeed();

        Log.d(TAG, "setData: " + str);

        if (str != null) {
            str += " km/h";
            wind.setText(str);
            speechText += ". Wind Speed " + str;
        } else
            wind.setText("N/A");

        if (data.getMain().getHumidity() != null) {
            humidity.setText(data.getMain().getHumidity() + "%");
            speechText += ". Humidity " + data.getMain().getHumidity() + "%";
        } else
            humidity.setText("N/A");

        if (data.getMain().getPressure() != null) {
            pressure.setText(data.getMain().getPressure() + " hPa");
            speechText += ". Pressure " + data.getMain().getPressure() + " hPa";
        } else
            pressure.setText("N/A");

        String min = data.getMain().getMin() != null ? data.getMain().getMin() + "°C" : "N/A";
        String max = data.getMain().getMax() != null ? data.getMain().getMax() + "°C" : "N/A";

        minMax.setText(min + " / " + max);

        location.setText(data.getName());

        latLon.setText(data.getCoord().getLat() + " / " + data.getCoord().getLon());

        setTextToSpeech();
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

                        Location location = locationResult.getLastLocation();

                        lat = locationResult.getLastLocation().getLatitude();

                        Log.d(TAG, "lat: " + lat);

                        lon = locationResult.getLastLocation().getLongitude();

                        Log.d(TAG, "lon: " + lon);

                        moveCamera(new LatLng(lat, lon), 15);

                        getData();
                    }

                    super.onLocationResult(locationResult);
                }
            }, Looper.getMainLooper());
        }
    }

    private void setTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        Log.d("TTS", "Lang not Supported");
                    else
                        speech.setEnabled(true);
                } else
                    Log.i("TTS", "Init failed");

            }
        });

        speech.setOnClickListener(v -> {
            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
        });
    }

    public boolean checkServices() {
        Log.d(TAG, "Checking Google Services");

        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Services is Working");
            initMap();
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "Error that can be fixed");

            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(MainActivity.this, available, ERROR_DIALOG);

            dialog.show();
        } else
            Toast.makeText(this, "Can't make Map Request", Toast.LENGTH_SHORT).show();

        return false;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MainActivity.this);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: Called");
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        maps = googleMap;

        getLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        maps.setMyLocationEnabled(true);
        maps.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}