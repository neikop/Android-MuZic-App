package project.qhk.fpt.edu.vn.muzic.adapters.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.R;

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
        String linkSmall = song.getImageSmall();
        ImageLoader.getInstance().displayImage(linkSmall, imageSongSmall);
    }
}
