package com.app.huaweiapp.fragment;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.huaweiapp.R;
import com.app.huaweiapp.adapter.MovieViewAdapter;
import com.app.huaweiapp.adapter.MovieViewImageAdapter;
import com.app.huaweiapp.database.PopularMovies;
import com.app.huaweiapp.model.Movie;
import com.app.huaweiapp.permission.RequestLocationPermission;
import com.app.huaweiapp.request.ApiEndPoint;
import com.app.huaweiapp.request.ApiService;
import com.app.huaweiapp.request.GeoApiEndPoint;
import com.app.huaweiapp.request.GeoApiService;
import com.app.huaweiapp.response.MovieSearchResponse;
import com.app.huaweiapp.utils.Credentials;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.Tasks;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHomeMovie extends Fragment {


    Vector<Movie> topRatedMovieList = new Vector<>();
    Vector<Movie> popularMovieList = new Vector<>();
    View v;
    String countryCode = "";
    PopularMovies database = PopularMovies.getInstance();

    public static final String TAG = "LocationUpdatesCallback";
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SettingsClient mSettingsClient;

    public FragmentHomeMovie() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_movie, container, false);
        getTopRated();

        popularMovieList = database.getListPopularMovies();
        if(popularMovieList.isEmpty()){
            setUpLocation();
            requestLocationUpdatesWithCallback();
        }
        else{
            setUpPopularRecyclerView();
        }


        return v;
    }

    void setUpTopRatedRecyclerView(){
        RecyclerView topRatedRecyclerView = v.findViewById(R.id.rv_top_rated_movie);

        MovieViewImageAdapter adapter = new MovieViewImageAdapter(v.getContext(), topRatedMovieList);

        topRatedRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false);
        topRatedRecyclerView.setLayoutManager(layoutManager);
//        topRatedRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    void getTopRated(){
        ApiEndPoint movieApi = ApiService.getMovieApi();
        Call<MovieSearchResponse> responseCall =
                movieApi.getTopRated(
                        Credentials.API_KEY,
                        "1"
                );


        responseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if(response.code() == 200){
                    topRatedMovieList = new Vector<Movie>(new ArrayList<>(response.body().getMovies()));
                    setUpTopRatedRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {

            }
        });
    }

    void setUpPopularRecyclerView(){
        RecyclerView popularRecyclerView = v.findViewById(R.id.rv_popular_movie);

        MovieViewAdapter adapter = new MovieViewAdapter(v.getContext(), popularMovieList);

        popularRecyclerView.setAdapter(adapter);

        popularRecyclerView.setLayoutManager(new GridLayoutManager(v.getContext(), 2));
    }

    void getPopular(String countryCode){
        ApiEndPoint movieApi = ApiService.getMovieApi();
        Call<MovieSearchResponse> responseCall =
                movieApi.getPopular(
                        Credentials.API_KEY,
                        "1",
                        countryCode
                );

        Log.d("Loglog", "Link " + responseCall.request().url().toString());
        responseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if(response.code() == 200){
                    popularMovieList = new Vector<Movie>(new ArrayList<>(response.body().getMovies()));
                    for (Movie m:
                         popularMovieList) {
                        Log.d("Loglog",m.getOriginalTitle());
                    }
                    database.setListPopularMovies(popularMovieList);
                    setUpPopularRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {

            }
        });
    }

    // region Location Kit

    protected void setCountryCode(Double latitude, Double longitude){
        GeoApiEndPoint geoApi = GeoApiService.getGeoApi();
        Call<com.app.huaweiapp.model.Location> responseCall =
                geoApi.getLocation("json",latitude,longitude
                );
        Log.d("Loglog", "Link " + responseCall.request().url().toString());
        responseCall.enqueue(new Callback<com.app.huaweiapp.model.Location>() {
            @Override
            public void onResponse(Call<com.app.huaweiapp.model.Location> call, Response<com.app.huaweiapp.model.Location> response) {
                if(response.code() == 200){
                    com.app.huaweiapp.model.Location location = response.body();
                    com.app.huaweiapp.model.Address adr = location.getAddress();

                    countryCode = adr.getCountryCode().toUpperCase();
                    Log.d("Loglog", "CC >>>>> " + countryCode);
                }
                else{
                    try{
                        countryCode = "US";
                        Log.d("Loglog", "Error " + response.errorBody().toString());
                    }catch (Exception e){};
                }
                getPopular(countryCode);
            }

            @Override
            public void onFailure(Call<com.app.huaweiapp.model.Location> call, Throwable t) {

            }
        });
    }

    void setUpLocation(){
        RequestLocationPermission.requestLocationPermission(v.getContext());

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(v.getContext());
        mSettingsClient = LocationServices.getSettingsClient(v.getContext());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                Log.d("Loglog", "Longitude " + location.getLongitude() + " Latitude " + location.getLatitude());
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                setCountryCode(latitude, longitude);
                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        Log.d("Loglog", "onLocationAvailability isLocationAvailable:" + flag);
                    }
                    else {
                        Log.d("Loglog", "onLocationAvailability =:" + locationAvailability.getLocationStatus());
                    }
                }
            };
        }
    }

    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask = mSettingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.i(TAG, "check location settings success");
                    mFusedLocationProviderClient
                            .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(v.getContext(),  "requestLocationUpdatesWithCallback onSuccess", Toast.LENGTH_LONG).show();

                                    // Stop Location Updates after Success
                                    if(countryCode != ""){
                                        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG,"requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                }
                            });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.i(TAG,  "checkLocationSetting onFailure:" + e.getMessage());
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        //When the startResolutionForResult is invoked, a dialog box is displayed, asking you to open the corresponding permission.
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) v.getContext(), 0);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.e(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i(TAG,  "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }
    // endregion
}