package com.app.huaweiapp.request;

import com.app.huaweiapp.model.Location;
import com.app.huaweiapp.model.Movie;
import com.app.huaweiapp.response.MovieSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeoApiEndPoint {


    @GET("/reverse?")
    Call<Location> getLocation(
            @Query("format") String format,
            @Query("lat") double lat,
            @Query("lon") double longitude
    );


}