package com.delta.hackathon.models;

import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("main")
    private String main;

    @SerializedName("description")
    private String desc;

    @SerializedName("icon")
    private String icon;

    public Weather(String main, String desc, String icon) {
        this.main = main;
        this.desc = desc;
        this.icon = icon;
    }

    public String getMain() {
        return main;
    }

    public String getDesc() {
        return desc;
    }

    public String getIcon() {
        return icon;
    }
}
