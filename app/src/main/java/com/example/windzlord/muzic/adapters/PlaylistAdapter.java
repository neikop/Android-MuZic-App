package com.example.windzlord.muzic.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.adapters.view_holders.PlaylistViewHolder;
import com.example.windzlord.muzic.managers.RealmManager;

/**
 * Created by WindzLord on 11/2/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.view_holder_playlist, parent, false);
        return new PlaylistViewHolder(parent.getContext(), itemView);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        holder.bind(RealmManager.getInstance().getAllPlaylist().get(position));
    }

    @Override
    public int getItemCount() {
        return RealmManager.getInstance().getAllPlaylist().size();
    }
}
