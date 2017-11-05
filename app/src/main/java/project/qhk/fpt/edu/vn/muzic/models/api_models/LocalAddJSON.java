package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.models.Song;

/**
 * Created by WindyKiss on 11/5/2017.
 */

public class LocalAddJSON {
    @SerializedName("token")
    private String token;

    @SerializedName("playlistId")
    private String id;

    @SerializedName("playlistName")
    private String name;

    @SerializedName("song")
    private SongJSON song;

    public LocalAddJSON(String id, String name, Song song) {
        this.token = PreferenceManager.getInstance().getToken();
        this.id = id;
        this.name = name;
        this.song = new SongJSON(song.getName(), song.getArtist(), song.getStream(), song.getImageLink());
    }

    public class SongJSON {
        @SerializedName("name")
        private String name;

        @SerializedName("artist")
        private String artist;

        @SerializedName("url")
        private String url;

        @SerializedName("thumbnail")
        private String thumbnail;

        public SongJSON(String name, String artist, String url, String thumbnail) {
            this.name = name;
            this.artist = artist;
            this.url = url;
            this.thumbnail = thumbnail;
        }
    }

}
