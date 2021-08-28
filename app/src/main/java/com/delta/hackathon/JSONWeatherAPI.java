package com.delta.hackathon;

import com.delta.hackathon.models.FinalData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JSONWeatherAPI {

    @GET("weather")
    Call<FinalData> getUsingCoord(@Query("lat") double lat,
                                  @Query("lon") double lon,
                                  @Query("appid") String appid,
                                  @Query("units") String units);

    @GET("weather")
    Call<FinalData> getUsingLocation(@Query("q") String q, @Query("appid") String appid, @Query("units") String units);
}
