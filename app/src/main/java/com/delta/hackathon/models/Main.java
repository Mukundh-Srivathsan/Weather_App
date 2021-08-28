package com.delta.hackathon.models;

import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    private String temp;

    @SerializedName("temp_min")
    private String min;

    @SerializedName("temp_man")
    private String max;

    @SerializedName("pressure")
    private String pressure;

    @SerializedName("humidity")
    private String humidity;

    public Main(String temp, String min, String max, String pressure, String humidity) {
        this.temp = temp;
        this.min = min;
        this.max = max;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public String getTemp() {
        return temp;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }
}
