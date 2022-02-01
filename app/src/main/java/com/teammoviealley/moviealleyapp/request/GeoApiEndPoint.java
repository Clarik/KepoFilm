package com.teammoviealley.moviealleyapp.request;

import com.teammoviealley.moviealleyapp.model.Location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoApiEndPoint {


    @GET("/reverse?")
    Call<Location> getLocation(
            @Query("format") String format,
            @Query("lat") double lat,
            @Query("lon") double longitude
    );


}