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
