package com.teammoviealley.moviealleyapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FavoriteMovie {

    private Integer id;

    private String title;

    private String posterPath;

    private String email;

    public FavoriteMovie(String email, Integer id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getEmail() {
        return email;
    }
}
