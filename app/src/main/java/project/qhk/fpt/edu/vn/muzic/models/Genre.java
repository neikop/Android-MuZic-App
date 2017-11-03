package project.qhk.fpt.edu.vn.muzic.models;

import io.realm.RealmObject;

/**
 * Created by WindzLord on 10/28/2017.
 */

public class Genre extends RealmObject {

    public final static String FIELD_TYPE = "type";
    public final static String FIELD_ALIVE = "alive";
    public final static String TYPE_GENRE = "GENRE";
    public final static String TYPE_PLAYLIST = "PLAYLIST";

    private String type;
    private int index;
    private String genreID;
    private String name;
    private boolean alive;

    public static Genre createGenre(String line) {
        Genre genre = new Genre();
        genre.type = TYPE_GENRE;
        genre.alive = true;
        genre.setGenreID(line.split(":")[0]);
        genre.setName(line.split(":")[1]);
        return genre;
    }

    public static Genre createPlaylist(String name) {
        Genre genre = new Genre();
        genre.type = TYPE_PLAYLIST;
        genre.alive = true;
        genre.setName(name);
        return genre;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getGenreID() {
        return genreID;
    }

    public void setGenreID(String genreID) {
        this.genreID = genreID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void goDie() {
        this.alive = false;
    }
}
