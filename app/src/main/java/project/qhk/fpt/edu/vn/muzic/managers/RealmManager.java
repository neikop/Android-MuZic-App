package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;

import java.util.List;

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
        genre.setIndex(getAliveGenres().size());
        getRealm().copyToRealm(genre);
        commitTransaction();
    }

    public void addPlaylist(Genre genre) {
        beginTransaction();
        List<Genre> alivePlaylist = getRealm().where(Genre.class)
                .equalTo(Genre.FIELD_ALIVE, true)
                .equalTo(Genre.FIELD_TYPE, Genre.TYPE_PLAYLIST)
                .findAll();
        genre.setGenreID(Genre.TYPE_PLAYLIST + (alivePlaylist.size() + 1));
        getRealm().copyToRealm(genre);
        commitTransaction();
    }

    public void addNewPlaylist(Playlist playlist) {
        beginTransaction();
        List<Playlist> alivePlaylist = getRealm().where(Playlist.class)
                .equalTo(Playlist.FIELD_ALIVE, true)
                .findAll();
        playlist.setPlaylistID("PLAYLIST"+(alivePlaylist.size() + 1));
        getRealm().copyToRealm(playlist);
        commitTransaction();
    }

    public void clearAllPlaylist(){
        beginTransaction();
        getRealm().where(Playlist.class)
                .findAll().deleteAllFromRealm();
        commitTransaction();
    }

    public List<Genre> getAliveGenres() {
        return getRealm().where(Genre.class)
                .equalTo(Genre.FIELD_ALIVE, true)
                .equalTo(Genre.FIELD_TYPE, Genre.TYPE_GENRE)
                .findAll();
    }

    public List<Genre> getAlivePlaylist() {
        List<Genre> genreList = getRealm().where(Genre.class)
                .equalTo(Genre.FIELD_ALIVE, true)
                .equalTo(Genre.FIELD_TYPE, Genre.TYPE_PLAYLIST)
                .findAll();
        beginTransaction();
        for (int i = 0; i < genreList.size(); i++) {
            genreList.get(i).setIndex(i);
        }
        commitTransaction();
        return genreList;
    }

    public List<Playlist> getAllPlaylist() {
        List<Playlist> playList = getRealm().where(Playlist.class)
                .equalTo(Playlist.FIELD_ALIVE, true)
                .findAll();
        beginTransaction();
        for (int i = 0; i < playList.size(); i++) {
            playList.get(i).setIndex(i);
        }
        commitTransaction();
        return playList;
    }

    public void addSong(Song song) {
        beginTransaction();
        System.out.println("playlist id: " + song.getPlaylistID());
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
                .equalTo(Song.GENRE_ID, genreID)
                .equalTo(Song.FIELD_ALIVE, true)
                .findAll();
    }

    public List<Song> getSongsOfPlaylist(String playlistId) {
        return getRealm().where(Song.class)
                .equalTo(Song.PLAYLIST_ID, playlistId)
                .equalTo(Song.FIELD_ALIVE, true)
                .findAll();
    }

    public void clearTopSong() {
        beginTransaction();
        getRealm().where(Song.class)
                .not().beginsWith(Song.PLAYLIST_ID, Genre.TYPE_PLAYLIST)
                .findAll().deleteAllFromRealm();
        commitTransaction();
    }

    public void removeGenre(Genre genre) {
        beginTransaction();
        for (Song song : getSongs(genre.getGenreID())) song.setGenreID("0");
        genre.goDie();
        commitTransaction();
    }

    public void clearSearch() {
        beginTransaction();
        for (Song song : getSongs("SEARCH")) song.goDie();
        commitTransaction();
    }

    public void renameGenre(Genre genre, String name) {
        beginTransaction();
        genre.setName(name);
        commitTransaction();
    }

    public void removePlaylist(Playlist playlist) {
        beginTransaction();
        for (Song song : getSongsOfPlaylist(playlist.getPlaylistID())) song.setPlaylistID("0");
        playlist.goDie();
        commitTransaction();
    }

    public void renamePlaylist(Playlist playlist, String name) {
        beginTransaction();
        playlist.setName(name);
        commitTransaction();
    }

    public void removeFavourSong(Song song) {
        beginTransaction();
        song.setGenreID("DEAD");
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
