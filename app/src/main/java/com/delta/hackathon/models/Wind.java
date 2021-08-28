package com.delta.hackathon.models;

import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    private String speed;

    public Wind(String speed) {
        this.speed = speed;
    }

    public String getSpeed() {
        return speed;
    }
}
