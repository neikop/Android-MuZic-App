package project.qhk.fpt.edu.vn.muzic.models;

import io.realm.RealmObject;

/**
 * Created by WindzLord on 10/28/2017.
 */

public class Genre extends RealmObject {

    private String number;
    private String name;

    public static Genre create(String line) {
        Genre genre = new Genre();
        genre.setNumber(line.split(":")[0]);
        genre.setName(line.split(":")[1]);
        return genre;
    }

    public static Genre create(String number, String name) {
        Genre genre = new Genre();
        genre.setNumber(number);
        genre.setName(name);
        return genre;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
