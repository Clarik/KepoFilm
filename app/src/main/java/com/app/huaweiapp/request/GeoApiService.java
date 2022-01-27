package com.app.huaweiapp.request;

import com.app.huaweiapp.utils.Credentials;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoApiService {
    public static final String BASE_URL = "https://nominatim.openstreetmap.org";

    public static final String API_KEY = "cd327344d1c9a719f78166637986ce81";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static GeoApiEndPoint geoApi = retrofit.create(GeoApiEndPoint.class);

    public static GeoApiEndPoint getGeoApi(){
        return geoApi;
    }
}
