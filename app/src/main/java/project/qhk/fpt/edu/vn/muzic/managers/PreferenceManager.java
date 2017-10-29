package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by WindzLord on 11/17/2016.
 */

public class PreferenceManager {

    private static final String KEY = "MuZicApplication";

    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    private static PreferenceManager instance;

    public static PreferenceManager getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new PreferenceManager(context);
    }

    public String[] getGenres() {
        return "2:Blues|3:Comedy|4:Children's Music|5:Classical|6:Country|7:Electronic|8:Holiday|11:Jazz|12:Latino|13:New Age|14:Pop|17:Dance|18:Hip-Hop/Rap|19:World|20:Alternative|21:Rock|23:Vocal|25:Easy Listening|27:J-Pop|28:Enka|29:Anime|30:Kayokyoku|50:Fitness Workout|52:Karaoke|53:Instrumental|1232:Chinese|1243:Korean|1262:Indian|50000063:Disney".split("|");
    }

}
