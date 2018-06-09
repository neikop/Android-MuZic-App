package com.example.windzlord.muzic.adapters.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.models.Song;

/**
 * Created by WindzLord on 11/29/2016.
 */

public class SongViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_song_small)
    ImageView imageSongSmall;

    @BindView(R.id.text_song_name)
    TextView textSongName;

    @BindView(R.id.text_song_artist)
    TextView textSongArtist;

    public SongViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Song song) {
        textSongName.setText(song.getName());
        textSongArtist.setText(song.getArtist());
        String imageLink = song.getImageLink();
        if (imageLink != null)
            ImageLoader.getInstance().displayImage(imageLink, imageSongSmall);
        else imageSongSmall.setImageResource(R.drawable.image_song_demo);
    }
}
