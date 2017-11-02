package project.qhk.fpt.edu.vn.muzic.notifiers;

/**
 * Created by WindzLord on 11/1/2017.
 */

public class UpdateNotifier {

    private String target;
    private String genreID;
    private boolean success;

    public UpdateNotifier(String target, String genreID, boolean success) {
        this.target = target;
        this.genreID = genreID;
        this.success = success;
    }

    public String getTarget() {
        return target;
    }

    public String getGenreID() {
        return genreID;
    }

    public boolean isSuccess() {
        return success;
    }

}
