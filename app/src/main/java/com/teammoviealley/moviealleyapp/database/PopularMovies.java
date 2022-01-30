package com.teammoviealley.moviealleyapp.database;

import com.teammoviealley.moviealleyapp.model.Movie;

import java.util.Vector;

public class PopularMovies {
    private static PopularMovies popularMovies = null;

    private String countryName = "";
    private Vector<Movie> listPopularMovies = new Vector<>();

    private PopularMovies(){

    }

    public static PopularMovies getInstance(){
        if(popularMovies == null){
            popularMovies = new PopularMovies();
        }
        return popularMovies;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setListPopularMovies(Vector<Movie> list) {
        this.listPopularMovies = list;
    }

    public Vector<Movie> getListPopularMovies(){
        return this.listPopularMovies;
    }

    public String getCountryName(){
        return this.countryName;
    }
}
