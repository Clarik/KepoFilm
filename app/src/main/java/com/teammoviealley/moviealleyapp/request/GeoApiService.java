package com.teammoviealley.moviealleyapp.request;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoApiService {
    public static final String BASE_URL = "https://nominatim.openstreetmap.org";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static GeoApiEndPoint geoApi = retrofit.create(GeoApiEndPoint.class);

    public static GeoApiEndPoint getGeoApi(){
        return geoApi;
    }
}
