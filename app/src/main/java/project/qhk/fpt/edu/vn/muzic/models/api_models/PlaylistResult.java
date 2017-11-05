package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by QuangTM on 11/2/2017.
 */

public class PlaylistResult {

    @SerializedName("status")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("playlists")
    private ArrayList<Playlist> playlists;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public class Playlist {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("listsong")
        private ArrayList<Song> songList;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ArrayList<Song> getSongList() {
            return songList;
        }

        public class Song {
            @SerializedName("_id")
            private String id;

            @SerializedName("name")
            private String name;

            @SerializedName("url")
            private String url;

            @SerializedName("thumbnail")
            private String thumbnail;

            @SerializedName("artist")
            private String artist;

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public String getStream() {
                return url;
            }

            public String getThumbnail() {
                return thumbnail;
            }

            public String getArtist() {
                return artist;
            }

        }
    }
}
