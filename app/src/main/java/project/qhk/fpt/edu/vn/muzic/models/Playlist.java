package project.qhk.fpt.edu.vn.muzic.models;

import io.realm.RealmObject;

/**
 * Created by QuangTM on 03/11/2017.
 */

public class Playlist extends RealmObject{

    public final static String FIELD_ALIVE = "alive";

    private String _id;
    private String playlistID;
    private int index;
    private String name;
    private boolean alive;

    public static Playlist createPlaylist(String name) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.alive = true;
        return playlist;
    }

    public Playlist() {

    }

    public Playlist(String name) {
        this.name = name;
        this.alive = true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaylistID() {
        return this.playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void goDie() {
        this.alive = false;
    }
}
