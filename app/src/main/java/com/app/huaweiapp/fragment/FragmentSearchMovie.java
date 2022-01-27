package com.app.huaweiapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.app.huaweiapp.R;
import com.app.huaweiapp.adapter.MovieSearchAdapter;
import com.app.huaweiapp.model.Movie;
import com.app.huaweiapp.request.ApiEndPoint;
import com.app.huaweiapp.request.ApiService;
import com.app.huaweiapp.response.MovieSearchResponse;
import com.app.huaweiapp.utils.Credentials;

import java.util.ArrayList;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSearchMovie extends Fragment {


    public FragmentSearchMovie() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    View v;

    SearchView search;

    TextView tv_emptyText;

    Vector<Movie> movieList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search_movie, container, false);

        setUp();
        return v;
    }

    void setUp(){
        tv_emptyText = v.findViewById(R.id.tvEmptyTextSearchFragment);

        search = v.findViewById(R.id.searchFilm);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getSearchedMovie(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getSearchedMovie(s);
                return false;
            }
        });
    }

    private void getSearchedMovie(String query){
        ApiEndPoint movieApi = ApiService.getMovieApi();
        Call<MovieSearchResponse> responseCall =
                movieApi.searchMovie(
                        Credentials.API_KEY,
                        query,
                        "1"
                );

        responseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if(response.code() == 200){
                    movieList = new Vector<Movie>(new ArrayList<>(response.body().getMovies()));
                    setUpRecyclerView();
                }
                else{
                    try{
                        Log.d("Tag", "Error " + response.errorBody().toString());
                    }catch (Exception e){

                    };
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {

            }
        });

    }

    void setUpRecyclerView(){
        RecyclerView searchRecyclerView = v.findViewById(R.id.rv_search_movie);

        MovieSearchAdapter adapter = new MovieSearchAdapter(v.getContext(), movieList);

        tv_emptyText.setText(movieList.isEmpty() ? "Type what movie you want to search" : "");

        searchRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecyclerView.setLayoutManager(layoutManager);
    }

}