package project.qhk.fpt.edu.vn.muzic.notifiers;

/**
 * Created by WindzLord on 12/2/2016.
 */

public class SongChanger {

    private String target;
    private int indexGenre;
    private int indexSong;

    public SongChanger(String target, int indexGenre, int indexSong) {
        this.target = target;
        this.indexGenre = indexGenre;
        this.indexSong = indexSong;
    }

    public String getTarget() {
        return target;
    }

    public int getIndexGenre() {
        return indexGenre;
    }

    public int getIndexSong() {
        return indexSong;
    }
}
