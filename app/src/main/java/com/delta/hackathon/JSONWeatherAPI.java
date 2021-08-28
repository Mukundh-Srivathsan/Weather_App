package com.delta.hackathon;

import com.delta.hackathon.models.FinalData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JSONWeatherAPI {

    @GET("weather")
    Call<FinalData> getUsingCoord(@Query("lat") String lat,
                                  @Query("lon") String lon,
                                  @Query("appid") String appid);

    @GET("weather")
    Call<FinalData> getUsingLocation(@Query("q") String q, @Query("appid") String appid);
}