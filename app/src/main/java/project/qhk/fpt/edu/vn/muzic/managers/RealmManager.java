package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;

import java.util.List;

import io.realm.Realm;
import project.qhk.fpt.edu.vn.muzic.models.Genre;

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

    public void add(Genre genre) {
        beginTransaction();
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
