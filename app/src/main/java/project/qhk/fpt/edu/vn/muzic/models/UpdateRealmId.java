package project.qhk.fpt.edu.vn.muzic.models;

/**
 * Created by WindyKiss on 11/5/2017.
 */

public class UpdateRealmId {
    private String playlistId;
    private String SongId;

    public UpdateRealmId() {
    }

    public UpdateRealmId(String playlistId, String songId) {
        this.playlistId = playlistId;
        SongId = songId;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getSongId() {
        return SongId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void setSongId(String songId) {
        SongId = songId;
    }
}
