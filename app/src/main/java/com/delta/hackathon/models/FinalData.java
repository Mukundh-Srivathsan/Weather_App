package com.delta.hackathon.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FinalData {

    @SerializedName("coord")
    private Coord coord;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("name")
    private String name;

    public FinalData(Coord coord, List<Weather> weather, Main main, Wind wind, String name) {
        this.coord = coord;
        this.weather = weather;
        this.main = main;
        this.wind = wind;
        this.name = name;
    }

    public Coord getCoord() {
        return coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public String getName() {
        return name;
    }
}
