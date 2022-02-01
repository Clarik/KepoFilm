package com.teammoviealley.moviealleyapp.model;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

@PrimaryKeys({"email"})
public class StoreFavoriteMovie extends CloudDBZoneObject {

    String email;
    String favoriteMovies;

    public StoreFavoriteMovie(String email, String json){
        super(StoreFavoriteMovie.class);
        this.email = email;
        this.favoriteMovies = json;
    }

    public String getEmail() {
        return email;
    }

    public String getFavoriteMovies() {
        return favoriteMovies;
    }
}
