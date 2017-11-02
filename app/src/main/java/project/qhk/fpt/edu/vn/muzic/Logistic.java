package project.qhk.fpt.edu.vn.muzic;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by WindzLord on 10/28/2017.
 */

public class Logistic {

    public final static String TITLE = "Explore";
    public final static String GENRES = "GENRES";
    public final static String PLAYLIST = "PLAYLIST";
    public final static String OFFLINE = "OFFLINE";
    public final static Integer MAX_SONG = 50;

    public final static String TOP_SONG_API = "https://iTunes.apple.com";

    public final static String GET_MP3_API = "http://103.1.209.134";

    public static String toTime(long time) {
        time = time / 1000;
        long min = time / 60;
        long sec = time - min * 60;
        return "" + (min < 10 ? ("0" + min) : ("" + min))
                + ":" + (sec < 10 ? ("0" + sec) : ("" + sec));
    }

    public static RotateAnimation getRotateAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }
}
