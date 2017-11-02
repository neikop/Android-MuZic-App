package project.qhk.fpt.edu.vn.muzic.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by WindzLord on 11/1/2017.
 */

public class Playlist extends RealmObject {

    private int index;
    private String name;
    private int number;
    private boolean alive;
    private RealmList<Song> listSong;

    public static Playlist create(String name) {
        Playlist playlist = new Playlist();
        playlist.name = name;
        playlist.alive = true;
        playlist.listSong = new RealmList<>();
        return playlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number + "";
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public RealmList<Song> getListSong() {
        return listSong;
    }

    public void addSong(Song song) {
        listSong.add(song);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void goDie() {
        this.alive = false;
    }
}
