package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Playlist;
import project.qhk.fpt.edu.vn.muzic.models.Song;

/**
 * Created by WindyKiss on 11/5/2017.
 */

public class LocalSyncJSON {
    @SerializedName("token")
    private String token;

    @SerializedName("playlists")
    private List<PlaylistJSON> playlists;

    public class PlaylistJSON {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("songs")
        private List<SongJSON> songs;

        public PlaylistJSON(String id, String name, List<SongJSON> songs) {
            this.id = id;
            this.name = name;
            this.songs = songs;
        }

        public PlaylistJSON() {
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

    public LocalSyncJSON() {
        this.token = PreferenceManager.getInstance().getToken();
        List<PlaylistJSON> playlistJSONs = new ArrayList<>();
        for (Playlist playlist : RealmManager.getInstance().getAllPlaylist()){
            List<PlaylistJSON.SongJSON> songJSONList = new ArrayList<>();
            //noinspection Convert2streamapi
            for (Song song : RealmManager.getInstance().getSongsPlaylist(playlist.getPlaylistID())){
                songJSONList.add(new PlaylistJSON().new SongJSON(song.getName(),song.getArtist(),song.getStream(), song.getImageLink()));
            }
            PlaylistJSON playlistJSON = new PlaylistJSON(playlist.get_id(), playlist.getName(), songJSONList);
            playlistJSONs.add(playlistJSON);
        }
        this.playlists = playlistJSONs;
    }

}
