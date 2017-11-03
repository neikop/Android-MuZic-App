package project.qhk.fpt.edu.vn.muzic.notifiers;

import project.qhk.fpt.edu.vn.muzic.models.Genre;

/**
 * Created by WindzLord on 12/2/2016.
 */

public class SongChanger {

    private String target;
    private Genre genre;
    private int indexSong;

    public SongChanger(String target, Genre genre, int indexSong) {
        this.target = target;
        this.genre = genre;
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

}
