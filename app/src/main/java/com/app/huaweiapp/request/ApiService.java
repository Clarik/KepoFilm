package com.app.huaweiapp.request;

import com.app.huaweiapp.utils.Credentials;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    public static final String BASE_URL = "https://api.themoviedb.org";

    public static final String API_KEY = "cd327344d1c9a719f78166637986ce81";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Credentials.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static ApiEndPoint movieApi = retrofit.create(ApiEndPoint.class);

    public static ApiEndPoint getMovieApi(){
        return movieApi;
    }
}
