package com.app.huaweiapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.huaweiapp.R;
import com.app.huaweiapp.model.Cast;
import com.app.huaweiapp.model.Movie;
import com.bumptech.glide.Glide;

import java.util.Vector;

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.CastHolder> {

    LayoutInflater mInflater;
    Vector<Cast> castList;

    public MovieCastAdapter(Context context, Vector<Cast> castList) {
        mInflater = LayoutInflater.from(context);
        this.castList = castList;
    }

    @Override
    public CastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_cast_item_template, parent, false);
        return new MovieCastAdapter.CastHolder(view, this);
    }

    @Override
    public void onBindViewHolder(CastHolder holder, int position) {
        Cast cast = this.castList.get(position);

        holder.tvTemplateMovieCastName.setText(cast.getName());

        String image_path = "https://image.tmdb.org/t/p/w500/"
                + cast.getProfilePath();

        Glide.with(holder.itemView.getContext())
                .load(image_path)
                .into(holder.ivTemplateMovieCastImage);

    }

    @Override
    public int getItemCount() {
        return this.castList.size();
    }

    class CastHolder extends RecyclerView.ViewHolder{

        RecyclerView.Adapter adapter;

        ImageView ivTemplateMovieCastImage;
        TextView tvTemplateMovieCastName;

        public CastHolder(View itemView, MovieCastAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            setUpImageView(itemView);
            setUpTextView(itemView);
        }


        void setUpImageView(View itemView){
            ivTemplateMovieCastImage = itemView.findViewById(R.id.iv_movie_cast_image);
        }

        void setUpTextView(View itemView){
            tvTemplateMovieCastName = itemView.findViewById(R.id.tv_movie_cast_name);
        }
    }
}
