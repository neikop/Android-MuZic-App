package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
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

    public void clearTopSong() {
        beginTransaction();
        getRealm().where(Song.class)
                .not().beginsWith(Song.GENRE_ID, Genre.TYPE_PLAYLIST)
                .findAll().deleteAllFromRealm();
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
