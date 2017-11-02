package project.qhk.fpt.edu.vn.muzic.models;

import io.realm.RealmObject;
import project.qhk.fpt.edu.vn.muzic.models.api_models.MediaFeed;
import project.qhk.fpt.edu.vn.muzic.models.api_models.SearchResult;

/**
 * Created by WindzLord on 12/1/2016.
 */

public class Song extends RealmObject {

    public final static String GENRE_ID = "genreID";
    public final static String FIELD_ALIVE = "alive";

    private String genreID;
    private String name;
    private String artist;
    private String imageLink;
    private String imagePicture;
    private String stream;
    private boolean alive = true;

    public Song() {
    }

    public Song(SearchResult.SearchSong searchSong) {
        this.name = searchSong.getTitle();
        this.artist = "";
        this.imageLink = searchSong.getImageLink();
        this.stream = searchSong.getStream();
    }

    public static Song create(String genreID, MediaFeed.Feed.Entry entry) {
        Song song = new Song();
        song.genreID = genreID;
        song.name = entry.getName();
        if (entry.getName().length() > 100) song.name = song.name.substring(0, 100);
        song.artist = entry.getArtist();
        song.imageLink = entry.getImageLink();
        return song;
    }

    public static Song create(String genreID, Song entry) {
        Song song = new Song();
        song.genreID = genreID;
        song.name = entry.getName();
        song.artist = entry.getArtist();
        song.imageLink = entry.getImageLink();
        song.stream = entry.getStream();
        return song;
    }

    public void setGenreID(String genreID) {
        this.genreID = genreID;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getImagePicture() {
        return imagePicture;
    }

    public void setImagePicture(String imagePicture) {
        this.imagePicture = imagePicture;
    }

    public String getStream() {
        return stream;
    }

    public void goDie() {
        alive = false;
    }
}
