package com.example.windzlord.muzic.models;

import io.realm.RealmObject;
import com.example.windzlord.muzic.models.api_models.MediaFeed;
import com.example.windzlord.muzic.models.api_models.PlaylistResult;
import com.example.windzlord.muzic.models.api_models.SearchResult;

/**
 * Created by WindzLord on 12/1/2016.
 */

public class Song extends RealmObject {

    public final static String GENRE_ID = "genreID";
    public final static String PLAYLIST_ID = "playlistID";
    public final static String FIELD_ALIVE = "alive";

    private String _id;
    private String genreID;
    private String playlistID;
    private String name;
    private String artist;
    private String imageLink;
    private String imagePicture;
    private String stream;
    private boolean alive = true;

    public Song() {
    }

    public Song(String _id, String name, String artist, String imageLink, String stream) {
        this._id = _id;
        this.name = name;
        this.artist = artist;
        this.imageLink = imageLink;
        this.stream = stream;
    }

    public Song(PlaylistResult.Playlist.Song song) {
        this._id = song.getId();
        this.name = song.getName();
        this.artist = song.getArtist();
        this.imageLink = song.getThumbnail();
        this.stream = song.getStream();
    }

    public Song(SearchResult.SearchSong searchSong) {
        this.name = searchSong.getTitle();
        this.artist = "";
        this.imageLink = searchSong.getImageLink();
        this.stream = searchSong.getStream();
    }

    public static Song create(String genreID, MediaFeed.Feed.Entry entry) {
        Song song = new Song();
        song.genreID = genreID;
        song.name = entry.getName();
        if (entry.getName().length() > 100) song.name = song.name.substring(0, 100);
        song.artist = entry.getArtist();
        song.imageLink = entry.getImageLink();
        return song;
    }

    public static Song create(String genreID, Song entry) {
        Song song = new Song();
        song.genreID = genreID;
        song.name = entry.getName();
        song.artist = entry.getArtist();
        song.imageLink = entry.getImageLink();
        song.stream = entry.getStream();
        return song;
    }

    public static Song createForPlaylist(String playlistID, Song entry) {
        Song song = new Song();
        song.setPlaylistID(playlistID);
        song._id = entry.get_id();
        song.name = entry.getName();
        song.artist = entry.getArtist();
        song.imageLink = entry.getImageLink();
        song.stream = entry.getStream();
        return song;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setGenreID(String genreID) {
        this.genreID = genreID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getPlaylistID() {
        return playlistID != null ? this.playlistID : "";
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getImagePicture() {
        return imagePicture;
    }

    public void setImagePicture(String imagePicture) {
        this.imagePicture = imagePicture;
    }

    public String getStream() {
        return stream;
    }

    public void goDie() {
        alive = false;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }
}
