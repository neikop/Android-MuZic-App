package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.models.Playlist;
import project.qhk.fpt.edu.vn.muzic.models.Song;

/**
 * Created by WindzLord on 11/29/2016.
 */

public class RealmManager {

    private static RealmManager instance;

    public static void init(Context context) {
        Realm.init(context);
        instance = new RealmManager();
    }

    public static RealmManager getInstance() {
        return instance;
    }

    public void addGenre(Genre genre) {
        beginTransaction();
        genre.setIndex(getGenres().size());
        getRealm().copyToRealm(genre);
        commitTransaction();
    }

    public List<Genre> getGenres() {
        return getRealm().where(Genre.class).findAll();
    }

    public void clearGenre() {
        beginTransaction();
        getRealm().delete(Genre.class);
        commitTransaction();
    }

    public void addSong(Song song) {
        beginTransaction();
        getRealm().copyToRealm(song);
        commitTransaction();
    }

    public void editSongPicture(Song song, String picture) {
        beginTransaction();
        song.setImagePicture(picture);
        commitTransaction();
    }

    public List<Song> getSongs(String genreID) {
        return getRealm().where(Song.class)
                .equalTo(Song.GENRE_ID, genreID, Case.INSENSITIVE)
                .findAll();
    }

    public void clearSong() {
        beginTransaction();
        getRealm().delete(Song.class);
        commitTransaction();
    }

    public void addPlaylist(Playlist playlist) {
        beginTransaction();
        playlist.setIndex(getRealm().where(Playlist.class).findAll().size());
        getRealm().copyToRealm(playlist);
        commitTransaction();
    }

    public void addPlaylist(Playlist playlist, Song song) {
        beginTransaction();
        playlist.setIndex(getRealm().where(Playlist.class).findAll().size());
        playlist.addSong(song);
        getRealm().copyToRealm(playlist);
        commitTransaction();
    }

    public List<Playlist> getAllPlaylistAlive() {
        List<Playlist> playlist = getRealm().where(Playlist.class)
                .equalTo("alive", true).findAll();
        beginTransaction();
        for (int i = 0; i < playlist.size(); i++) playlist.get(i).setNumber(i + 1);
        commitTransaction();
        return playlist;
    }

    public void addSongToList(Playlist playlist, Song song) {
        beginTransaction();
        playlist.addSong(song);
        commitTransaction();
    }

    public void renamePlaylist(Playlist playlist, String name) {
        beginTransaction();
        playlist.setName(name);
        commitTransaction();
    }

    public void deletePlaylist(int indexPlaylist) {
        beginTransaction();
        getRealm().where(Playlist.class)
                .equalTo("index", indexPlaylist)
                .findFirst().goDie();
        commitTransaction();
    }

    public void clearPlaylist() {
        beginTransaction();
        getRealm().delete(Playlist.class);
        commitTransaction();
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    private void beginTransaction() {
        getRealm().beginTransaction();
    }

    private void commitTransaction() {
        getRealm().commitTransaction();
    }
}
