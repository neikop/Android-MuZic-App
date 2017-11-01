package project.qhk.fpt.edu.vn.muzic.objects;

/**
 * Created by WindzLord on 11/1/2017.
 */

public class UpdateNotifier {

    private String target;
    private String number;
    private boolean success;

    public UpdateNotifier(String target, String number, boolean success) {
        this.target = target;
        this.number = number;
        this.success = success;
    }

    public String getTarget() {
        return target;
    }

    public String getNumber() {
        return number;
    }

    public boolean isSuccess() {
        return success;
    }

}
