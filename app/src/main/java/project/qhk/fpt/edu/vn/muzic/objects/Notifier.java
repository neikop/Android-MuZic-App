package project.qhk.fpt.edu.vn.muzic.objects;

/**
 * Created by WindzLord on 12/1/2016.
 */

public class Notifier {

    private String source;
    private int checker;

    public Notifier(String source, int checker) {
        this.source = source;
        this.checker = checker;
    }

    public String getSource() {
        return source;
    }

    public int getChecker() {
        return checker;
    }
}
