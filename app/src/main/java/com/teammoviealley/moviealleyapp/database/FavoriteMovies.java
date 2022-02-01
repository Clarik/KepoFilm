package com.teammoviealley.moviealleyapp.database;


import com.teammoviealley.moviealleyapp.model.FavoriteMovie;

import java.util.Vector;

public class FavoriteMovies {
    private static FavoriteMovies instance = null;
    private Vector<FavoriteMovie> favoriteMovies = new Vector<>();

    public static FavoriteMovies getInstance(){
        if(instance == null){
            instance = new FavoriteMovies();
        }
        return instance;
    }

    public void setFavoriteMovies(Vector<FavoriteMovie> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public Vector<FavoriteMovie> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void add(FavoriteMovie f){
        this.favoriteMovies.add(f);
    }

    public boolean checkAndAdd(FavoriteMovie fm){
        for (FavoriteMovie m:
             favoriteMovies) {
            if(m.getId() == fm.getId()){
                return false;
            }
        }
        favoriteMovies.add(fm);
        return true;
    }
}
