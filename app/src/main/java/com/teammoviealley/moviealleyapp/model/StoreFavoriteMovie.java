package com.teammoviealley.moviealleyapp.model;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

@PrimaryKeys({"email"})
public class StoreFavoriteMovie extends CloudDBZoneObject {

    String email;
    String favorite_movie;

    public StoreFavoriteMovie(String email, String json){
        super(StoreFavoriteMovie.class);
        this.email = email;
        this.favorite_movie = json;
    }

    public String getEmail() {
        return email;
    }

    public String getFavorite_movie() {
        return favorite_movie;
    }
}
