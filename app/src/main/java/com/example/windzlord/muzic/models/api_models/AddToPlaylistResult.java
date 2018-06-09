package com.example.windzlord.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by QuangTM on 05/11/2017.
 */

public class AddToPlaylistResult {

    @SerializedName("status")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("playlist")
    private Playlist playlist;

    @SerializedName("song")
    private Song song;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getSongId() {
        return song.get_id();
    }

    public String getPlaylistId() {
        return playlist.getId();
    }

    public class Playlist {

        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Song {

        @SerializedName("_id")
        private String _id;

        public String get_id() {
            return _id;
        }
    }
}
