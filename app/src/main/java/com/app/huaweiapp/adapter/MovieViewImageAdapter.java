package com.app.huaweiapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.huaweiapp.MovieDetail;
import com.app.huaweiapp.R;
import com.app.huaweiapp.model.Movie;
import com.bumptech.glide.Glide;

import java.util.Vector;

public class MovieViewImageAdapter extends RecyclerView.Adapter<MovieViewImageAdapter.MovieHolder>{

    LayoutInflater mInflater;
    Vector<Movie> movieList;

    public MovieViewImageAdapter(Context context, Vector<Movie> movieList) {
        mInflater = LayoutInflater.from(context);
        this.movieList = movieList;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_item_image_template, parent, false);
        return new MovieHolder(view, this);
    }

    @Override
    public void onBindViewHolder(MovieViewImageAdapter.MovieHolder holder, int position) {
        Movie movie = this.movieList.get(position);

        holder.setMovie_id(movie.getId());

        String image_path = "https://image.tmdb.org/t/p/w500/"
                + movie.getPoster_path();

        Glide.with(holder.itemView.getContext())
                .load(image_path)
                .into(holder.ivTemplateMovieImage);

    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RecyclerView.Adapter adapter;

        ImageView ivTemplateMovieImage;

        Integer movie_id;

        public MovieHolder(View itemView, MovieViewImageAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            setUpImageView(itemView);

            itemView.setOnClickListener(this);
        }

        void setUpImageView(View itemView){
            ivTemplateMovieImage = itemView.findViewById(R.id.item_view_image);
        }

        public Integer getMovie_id() {
            return movie_id;
        }

        public void setMovie_id(Integer movie_id) {
            this.movie_id = movie_id;
        }

        @Override
        public void onClick(View view) {
            moveToMovieDetail(view);
        }

        public void moveToMovieDetail(View v){
            Intent moveToMovieDetailActivity = new Intent(v.getContext(), MovieDetail.class);
            moveToMovieDetailActivity.putExtra(MovieDetail.EXTRA_MOVIE_ID, getMovie_id());
            v.getContext().startActivity(moveToMovieDetailActivity);
        }
    }
}
