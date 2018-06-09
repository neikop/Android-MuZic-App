package com.example.windzlord.muzic.adapters.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.models.Playlist;

/**
 * Created by WindzLord on 11/2/2017.
 */

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_playlist_name)
    TextView textViewName;

    @BindView(R.id.text_playlist_number)
    TextView textViewNumber;

    public PlaylistViewHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Playlist playlist) {
        textViewName.setText(playlist.getName());
        textViewNumber.setText("" + (playlist.getIndex() + 1));
    }
}
