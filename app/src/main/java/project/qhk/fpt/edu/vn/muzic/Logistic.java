package project.qhk.fpt.edu.vn.muzic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by WindzLord on 10/28/2017.
 */

public class Logistic {

    public final static String TITLE = "Explore";
    public final static String GENRES = "GENRES";
    public final static String PLAYLIST = "PLAYLIST";
    public final static String SEARCH = "SEARCH";

    public final static String TOP_SONG_API = "https://iTunes.apple.com";

    public final static String SERVER_API = "https://qhkmusic.herokuapp.com";

    public final static String GET_MP3_API = "http://103.1.209.134";

    public final static String SEARCH__API = "https://api-v2.soundcloud.com";

    public final static String CLIENT_ID = "MbFtrpTYuwoPYLnPGQIFPahc1TNeVFnu";

    public static String toTime(long time) {
        time = time / 1000;
        long min = time / 60;
        long sec = time - min * 60;
        return "" + (min < 10 ? ("0" + min) : ("" + min)) + ":" + (sec < 10 ? ("0" + sec) : ("" + sec));
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

    public static void setImagePicture(Context context, ImageView imageView, String name) {
        try {
            InputStream stream = context.getAssets().open(name);
            Drawable drawable = Drawable.createFromStream(stream, null);
            imageView.setImageDrawable(drawable);
            stream.close();
        } catch (IOException ex) {

        }
    }

}
