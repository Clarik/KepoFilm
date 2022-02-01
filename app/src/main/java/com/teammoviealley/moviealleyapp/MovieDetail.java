package com.teammoviealley.moviealleyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.teammoviealley.moviealleyapp.adapter.MovieCastAdapter;
import com.teammoviealley.moviealleyapp.database.FavoriteMovies;
import com.teammoviealley.moviealleyapp.model.Cast;
import com.teammoviealley.moviealleyapp.model.FavoriteMovie;
import com.teammoviealley.moviealleyapp.model.Genre;
import com.teammoviealley.moviealleyapp.model.Movie;
import com.teammoviealley.moviealleyapp.model.MovieTrailer;
import com.teammoviealley.moviealleyapp.model.MovieTrailerVideos;
import com.teammoviealley.moviealleyapp.model.ObjectTypeInfoHelper;
import com.teammoviealley.moviealleyapp.model.StoreFavoriteMovie;
import com.teammoviealley.moviealleyapp.request.ApiEndPoint;
import com.teammoviealley.moviealleyapp.request.ApiService;
import com.teammoviealley.moviealleyapp.response.MovieCastResponse;
import com.teammoviealley.moviealleyapp.utils.Credentials;
import com.bumptech.glide.Glide;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;


import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "movie_id";

    TextView tvMovieTitle, tvMovieYear, tvMovieReleaseDate, tvMovieOverview, tvGenres;

    ImageView ivMovieImage;

    YouTubePlayerView vpMovieTrailer;
    Button btnFavorite;
    AGConnectCloudDB mCloudDB;
    CloudDBZone mCloudDBZone;
    CloudDBZoneConfig mConfig;
    Gson gson = new Gson();
    FavoriteMovies database = FavoriteMovies.getInstance();
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        setUpView();

        ApiEndPoint movieApi = ApiService.getMovieApi();
        getMovieWithID(movieApi);
        getMovieCast(movieApi);



        getMovieTrailer(movieApi);


        setUpAds();

    }

    void getMovieTrailer(ApiEndPoint movieApi){
        getLifecycle().addObserver(vpMovieTrailer);

        Call<MovieTrailerVideos> videoTrailerCall =
                movieApi.getMovieTrailer(
                        getIntent().getIntExtra(EXTRA_MOVIE_ID, 0)
                        , Credentials.API_KEY
                );

        videoTrailerCall.enqueue(new Callback<MovieTrailerVideos>() {
            @Override
            public void onResponse(Call<MovieTrailerVideos> call, Response<MovieTrailerVideos> response) {
                for(MovieTrailer movie : response.body().getResults()){
                    if(movie.getType().equalsIgnoreCase("Trailer"))
                    {
                        setUpVideoYoutube(movie.getKey());
                        break;
                    }
                }

            }

            @Override
            public void onFailure(Call<MovieTrailerVideos> call, Throwable t) {
            }

        });
    }

    void setUpVideoYoutube(String key){
        Log.d("YoutubeTest", "Key : " + key);

        vpMovieTrailer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(key, 0);
            }
        });
//
//        vpMovieTrailer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
////
//            }
//        });
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
        tvGenres = findViewById(R.id.tv_genres);

        ivMovieImage = findViewById(R.id.iv_movie_detail_image);
        btnFavorite = findViewById(R.id.btn_favorite);
        vpMovieTrailer = findViewById(R.id.vp_movie_trailer);
    }

    void setUpMovieCastRecyclerView(Vector<Cast> castList){
        RecyclerView movieCastRecyclerView = findViewById(R.id.rv_movie_cast);

        MovieCastAdapter adapter = new MovieCastAdapter(this, castList);

        movieCastRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        movieCastRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void getMovieCast(ApiEndPoint movieApi){
        Call<MovieCastResponse> responseCall =
                movieApi.getCast(
                        getIntent().getIntExtra(EXTRA_MOVIE_ID, 0),
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
        String genres = "";
        List<Genre> listGenre = movie.getGenres();
        for(int i = 0; i < listGenre.size(); i++){
            genres = genres + ((i > 0) ? ", " : "") + listGenre.get(i).getName();
        }

        tvGenres.setText(genres);
        tvMovieOverview.setText(movie.getOverview());
        String image_path = "https://image.tmdb.org/t/p/w500/"
                + movie.getPosterPath();

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavorite(movie.getId(), movie.getTitle(), movie.getPosterPath());
            }
        });


        Glide.with(this)
                .load(image_path)
                .into(ivMovieImage);
    }

    public void addFavorite(Integer id, String name, String path){
        FavoriteMovie fm = new FavoriteMovie(id, name, path);
        boolean success = database.checkAndAdd(fm);
        if(success){
            syncFavorite();
            Toast.makeText(this,  "Added to my Favorite", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,  "Already added to favorite", Toast.LENGTH_LONG).show();
        }
    }

    public void syncFavorite(){

        AccountAuthService mAuthService;
        AccountAuthParams mAuthParam;
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().createParams();
        mAuthService = AccountAuthManager.getService(this, mAuthParam);
        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                email = authAccount.getEmail().toString();
            }
        });
        //email = "test@binus.ac.id";
        Toast.makeText(this,  email, Toast.LENGTH_LONG).show();
        String json = gson.toJson(database.getFavoriteMovies());
        //String json = email;
        StoreFavoriteMovie store = new StoreFavoriteMovie(email,json);
        upsertToCloudDB(store);
    }

    public void upsertToCloudDB(StoreFavoriteMovie store){
        mCloudDB.initialize(this);
        mCloudDB = AGConnectCloudDB.getInstance();
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());

        } catch (AGConnectCloudDBException e) {

        }

        mConfig = new CloudDBZoneConfig("user",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);

        try {
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig, true);

        } catch (AGConnectCloudDBException e) {

        }


        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(store);
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.i("TAG", "Upsert " + cloudDBZoneResult + " records");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}