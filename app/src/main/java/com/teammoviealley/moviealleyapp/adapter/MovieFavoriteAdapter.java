package com.teammoviealley.moviealleyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teammoviealley.moviealleyapp.R;
import com.teammoviealley.moviealleyapp.model.FavoriteMovie;

import java.util.Vector;

public class MovieFavoriteAdapter extends RecyclerView.Adapter<MovieFavoriteAdapter.MovieHolder> {

    LayoutInflater mInflater;
    Vector<FavoriteMovie> favoriteList;

    public MovieFavoriteAdapter(Context context, Vector<FavoriteMovie> favoriteList) {
        mInflater = LayoutInflater.from(context);
        this.favoriteList = favoriteList;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_item_favorite_template, parent, false);
        return new MovieFavoriteAdapter.MovieHolder(view, this);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        FavoriteMovie favoriteMovie = this.favoriteList.get(position);

        holder.tvTemplateMovieFavoriteName.setText(favoriteMovie.getTitle());

        String image_path = "https://image.tmdb.org/t/p/w500/"
                + favoriteMovie.getPosterPath();

        Glide.with(holder.itemView.getContext())
                .load(image_path)
                .into(holder.ivTemplateMovieFavoriteImage);

    }

    @Override
    public int getItemCount() {
        return this.favoriteList.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder{

        RecyclerView.Adapter adapter;

        ImageView ivTemplateMovieFavoriteImage;
        TextView tvTemplateMovieFavoriteName;

        public MovieHolder(View itemView, MovieFavoriteAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            setUpImageView(itemView);
            setUpTextView(itemView);
        }


        void setUpImageView(View itemView){
            ivTemplateMovieFavoriteImage = itemView.findViewById(R.id.iv_movie_favorite_image);
        }

        void setUpTextView(View itemView){
            tvTemplateMovieFavoriteName = itemView.findViewById(R.id.tv_movie_favorite_title);
        }
    }
}