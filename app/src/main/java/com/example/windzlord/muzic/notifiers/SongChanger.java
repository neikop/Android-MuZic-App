package com.example.windzlord.muzic.notifiers;

import com.example.windzlord.muzic.models.Genre;
import com.example.windzlord.muzic.models.Playlist;

/**
 * Created by WindzLord on 12/2/2016.
 */

public class SongChanger {

    private String target;
    private Genre genre;
    private Playlist playlist;
    private int indexSong;

    public SongChanger(String target, Genre genre, int indexSong) {
        this.target = target;
        this.genre = genre;
        this.indexSong = indexSong;
    }

    public SongChanger(String target, Playlist playlist, int indexSong) {
        this.target = target;
        this.playlist = playlist;
        this.indexSong = indexSong;
    }

    public String getTarget() {
        return target;
    }

    public Genre getGenre() {
        return genre;
    }

    public int getIndexSong() {
        return indexSong;
    }

    public Playlist getPlaylist() {
        return playlist;
    }
}
