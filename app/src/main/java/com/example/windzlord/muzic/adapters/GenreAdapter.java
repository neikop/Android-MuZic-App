package com.example.windzlord.muzic.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.adapters.view_holders.GenreViewHolder;
import com.example.windzlord.muzic.managers.RealmManager;

/**
 * Created by WindzLord on 11/29/2016.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreViewHolder> {

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.view_holder_genre, parent, false);
        return new GenreViewHolder(parent.getContext(), itemView);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        holder.bind(RealmManager.getInstance().getAliveGenres().get(position));
    }

    @Override
    public int getItemCount() {
        return RealmManager.getInstance().getAliveGenres().size();
    }
}

