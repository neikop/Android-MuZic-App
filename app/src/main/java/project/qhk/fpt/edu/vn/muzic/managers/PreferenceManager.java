package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;
import android.content.SharedPreferences;

import project.qhk.fpt.edu.vn.muzic.models.api_models.LoginResult;

/**
 * Created by WindzLord on 11/17/2016.
 */

public class PreferenceManager {

    private static final String KEY = "PreferenceManager";

    private static final String USERNAME = "USERNAME";
    private static final String TOKEN = "TOKEN";
    private static final String EMAIL = "EMAIL";
    private static final String NICKNAME = "NICKNAME";

    private static PreferenceManager instance;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new PreferenceManager(context);
    }

    private void putUsername(String username) {
        sharedPreferences.edit().putString(USERNAME, username).apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME, "");
    }

    private void putToken(String token) {
        sharedPreferences.edit().putString(TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }

    private void putNickname(String nickname) {
        sharedPreferences.edit().putString(NICKNAME, nickname).apply();
    }

    public String getNickname() {
        return sharedPreferences.getString(NICKNAME, "");
    }

    private void putEmail(String email) {
        sharedPreferences.edit().putString(EMAIL, email).apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL, "");
    }

    public void goLogin(LoginResult result) {
        putNickname(result.getUser().getName());
        putEmail(result.getUser().getEmail());
        putUsername(result.getName());
        putToken(result.getToken());
    }

    public void goLogout() {
        putNickname("");
        putEmail("");
        putUsername("");
        putToken("");
    }

    public boolean isLogin() {
        return !getToken().isEmpty();
    }

    public String[] getGenres() {
        return "2:Blues|3:Comedy|4:Children's Music|5:Classical|6:Country|7:Electronic|8:Holiday|11:Jazz|12:Latino|13:New Age|14:Pop|17:Dance|18:Hip-Hop/Rap|19:World|20:Alternative|21:Rock|23:Vocal|25:Easy Listening|27:J-Pop|28:Enka|29:Anime|30:Kayokyoku|50:Fitness Workout|52:Karaoke|53:Instrumental|1232:Chinese|1243:Korean|1262:Indian|50000063:Disney".split("|");
    }

}
