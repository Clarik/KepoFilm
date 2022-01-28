package com.app.huaweiapp.request;

import com.app.huaweiapp.model.Credit;
import com.app.huaweiapp.model.Movie;
import com.app.huaweiapp.response.MovieCastResponse;
import com.app.huaweiapp.response.MovieSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndPoint {


    @GET("/3/movie/{movie_id}/credits")
    Call<MovieCastResponse> getCast(
            @Path("movie_id") Integer movie_id,
            @Query("api_key") String key
    );



    @GET("/3/movie/popular")
    Call<MovieSearchResponse> getPopular(
            @Query("api_key") String key,
            @Query("page") String page,
            @Query("region") String region
    );

    @GET("/3/movie/top_rated")
    Call<MovieSearchResponse> getTopRated(
            @Query("api_key") String key,
            @Query("page") String page
    );

    @GET("/3/search/movie")
    Call<MovieSearchResponse> searchMovie(
            @Query("api_key") String key,
            @Query("query") String query,
            @Query("page") String page
    );

    @GET("/3/movie/{movie_id}?")
    Call<Movie> getMovie(
            @Path("movie_id") Integer movie_id,
            @Query("api_key") String key
    );

}