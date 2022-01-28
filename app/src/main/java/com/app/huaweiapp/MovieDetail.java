package com.app.huaweiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.huaweiapp.adapter.MovieCastAdapter;
import com.app.huaweiapp.adapter.MovieViewImageAdapter;
import com.app.huaweiapp.model.Cast;
import com.app.huaweiapp.model.Credit;
import com.app.huaweiapp.model.Movie;
import com.app.huaweiapp.request.ApiEndPoint;
import com.app.huaweiapp.request.ApiService;
import com.app.huaweiapp.response.MovieCastResponse;
import com.app.huaweiapp.response.MovieSearchResponse;
import com.app.huaweiapp.utils.Credentials;
import com.bumptech.glide.Glide;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "movie_id";

    TextView tvMovieTitle, tvMovieYear, tvMovieReleaseDate, tvMovieOverview;

    ImageView ivMovieImage;

    Integer movie_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ApiEndPoint movieApi = ApiService.getMovieApi();
        getMovieWithID(movieApi);
        getMovieCast(movieApi);

        setUpAds();
    }

    void setUpAds(){
        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);

        // Obtain BannerView based on the configuration in layout/ad_fragment.xml.
        BannerView bottomBannerView = findViewById(R.id.hw_banner_view);
        AdParam adParam = new AdParam.Builder().build();
        bottomBannerView.loadAd(adParam);

        // Call new BannerView(Context context) to create a BannerView class.
//        BannerView topBannerView = new BannerView(this);
//        topBannerView.setAdId("testw6vs28auh3");
//        topBannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_SMART);
//        topBannerView.loadAd(adParam);
//
//        RelativeLayout rootView = findViewById(R.id.root_view);
//        rootView.addView(topBannerView);
    }

    void setUpView(){
        tvMovieTitle = findViewById(R.id.tv_movie_detail_title);
        tvMovieYear = findViewById(R.id.tv_movie_detail_year);
        tvMovieReleaseDate = findViewById(R.id.tv_movie_detail_release_date);
        tvMovieOverview = findViewById(R.id.tv_movie_detail_overview);

        ivMovieImage = findViewById(R.id.iv_movie_detail_image);

    }

    void setUpMovieCastRecyclerView(Vector<Cast> castList){
        RecyclerView movieCastRecyclerView = findViewById(R.id.rv_movie_cast);

        MovieCastAdapter adapter = new MovieCastAdapter(this, castList);

        movieCastRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        movieCastRecyclerView.setLayoutManager(layoutManager);
    }

    void getMovieCast(ApiEndPoint movieApi){
        Call<MovieCastResponse> responseCall =
                movieApi.getCast(
                        550,
                        Credentials.API_KEY
                );

        responseCall.enqueue(new Callback<MovieCastResponse>() {
            @Override
            public void onResponse(Call<MovieCastResponse> call, Response<MovieCastResponse> response) {
                if(response.code() == 200){
                    setUpMovieCastRecyclerView(new Vector<>(response.body().getCasts()));
                }
                else{
                    try{
                    }catch (Exception e){

                    };
                }
            }

            @Override
            public void onFailure(Call<MovieCastResponse> call, Throwable t) {

            }
        });
    }

    void getMovieWithID(ApiEndPoint movieApi){
        Call<Movie> responseCall =
                movieApi.getMovie(
                        getIntent().getIntExtra(EXTRA_MOVIE_ID, 0),
                        Credentials.API_KEY
                );
        responseCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(response.code() == 200){
                    setUpView();
                    showMovie(response.body());
                }
                else{
                    try{
                        Log.d("Tag", "Error " + response.errorBody().toString());
                    }catch (Exception e){};
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }

    void showMovie(Movie movie){
        tvMovieTitle.setText(movie.getTitle());

        String release_date = movie.getReleaseDate();
        String[] split = release_date.split("-");
        String release_year = split[0];

        tvMovieYear.setText(release_year);
        tvMovieReleaseDate.setText(release_date);

        tvMovieOverview.setText(movie.getOverview());

        String image_path = "https://image.tmdb.org/t/p/w500/"
                + movie.getPosterPath();

        Glide.with(this)
                .load(image_path)
                .into(ivMovieImage);
    }
}