package com.app.huaweiapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.huaweiapp.MovieDetail;
import com.app.huaweiapp.R;
import com.app.huaweiapp.model.Movie;
import com.bumptech.glide.Glide;

import java.util.Vector;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.MovieHolder>{

    LayoutInflater mInflater;
    Vector<Movie> movieList;

    public MovieSearchAdapter(Context context, Vector<Movie> movieList) {
        mInflater = LayoutInflater.from(context);
        this.movieList = movieList;
    }

    @Override
    public MovieSearchAdapter.MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_item_search_template, parent, false);
        return new MovieSearchAdapter.MovieHolder(view, this);
    }

    @Override
    public void onBindViewHolder(MovieSearchAdapter.MovieHolder holder, int position) {
        Movie movie = this.movieList.get(position);

        holder.setMovie_id(movie.getId());

        holder.tvSearchTemplateMovieTitle.setText(movie.getTitle());

        holder.tvSearchTemplateMovieOverview.setText(movie.getOverview());

        holder.tvSearchTemplateMovieReleaseDate.setText(movie.getRelease_date());

        String image_path = "https://image.tmdb.org/t/p/w500/"
                + movie.getPoster_path();

        Glide.with(holder.itemView.getContext())
                .load(image_path)
                .into(holder.ivSearchTemplateMovieImage);

    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RecyclerView.Adapter adapter;

        TextView tvSearchTemplateMovieTitle;
        TextView tvSearchTemplateMovieOverview;
        TextView tvSearchTemplateMovieReleaseDate;
        ImageView ivSearchTemplateMovieImage;

        Integer movie_id;

        public MovieHolder(View itemView, MovieSearchAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            setUpTextView(itemView);
            setUpImageView(itemView);

            itemView.setOnClickListener(this);
        }

        void setUpTextView(View itemView){
            tvSearchTemplateMovieTitle = itemView.findViewById(R.id.tv_movie_search_title);
            tvSearchTemplateMovieOverview = itemView.findViewById(R.id.tv_movie_search_overview);
            tvSearchTemplateMovieReleaseDate = itemView.findViewById(R.id.tv_movie_search_release_date);
        }

        void setUpImageView(View itemView){
            ivSearchTemplateMovieImage = itemView.findViewById(R.id.iv_movie_search_image);
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
