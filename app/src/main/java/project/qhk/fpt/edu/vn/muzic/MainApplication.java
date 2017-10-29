package project.qhk.fpt.edu.vn.muzic;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import project.qhk.fpt.edu.vn.muzic.managers.NetworkManager;
import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;

/**
 * Created by WindzLord on 10/28/2017.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        settingThingsUp();
    }

    private void settingThingsUp() {
        PreferenceManager.init(this);
        NetworkManager.init(this);
        RealmManager.init(this);
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).build());

        if (RealmManager.getInstance().getGenres().isEmpty())
            goGenre();
        goTopSong();
    }

    private void goGenre() {
        System.out.println("goGenre");
        RealmManager.getInstance().clearGenre();

        InputStream inputStream = getResources().openRawResource(R.raw.genre);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            while (bufferedReader.readLine() != null)
                RealmManager.getInstance().add(Genre.create(bufferedReader.readLine()));

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (IOException e) {
            for (String genre : PreferenceManager.getInstance().getGenres())
                RealmManager.getInstance().add(Genre.create(genre));
        }
    }

    private void goTopSong() {
        System.out.println("goTopSong");
    }
}
