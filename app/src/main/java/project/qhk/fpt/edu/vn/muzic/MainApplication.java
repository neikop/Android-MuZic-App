package project.qhk.fpt.edu.vn.muzic;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import project.qhk.fpt.edu.vn.muzic.managers.MusicPlayer;
import project.qhk.fpt.edu.vn.muzic.managers.NetworkManager;
import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.models.api_models.MediaFeed;
import project.qhk.fpt.edu.vn.muzic.objects.UpdateNotifier;
import project.qhk.fpt.edu.vn.muzic.screens.SongsFragment;
import project.qhk.fpt.edu.vn.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        MusicPlayer.init(this);

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
                RealmManager.getInstance().addGenre(Genre.create(bufferedReader.readLine()));

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (IOException e) {
            for (String genre : PreferenceManager.getInstance().getGenres())
                RealmManager.getInstance().addGenre(Genre.create(genre));
        }
    }

    private void goTopSong() {
        System.out.println("goTopSong");
        RealmManager.getInstance().clearSong();

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.TOP_SONG_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);

        for (Genre genre : RealmManager.getInstance().getGenres()) {

            musicService.getMediaFeed(genre.getNumber()).enqueue(new Callback<MediaFeed>() {
                @Override
                public void onResponse(Call<MediaFeed> call, Response<MediaFeed> response) {
                    for (MediaFeed.Feed.Entry entry : response.body().getTopSongList()) {
                        RealmManager.getInstance().addSong(Song.create(genre.getNumber(), entry));
                    }
                    EventBus.getDefault().post(new UpdateNotifier(
                            SongsFragment.class.getSimpleName(), genre.getNumber(), true));
                }

                @Override
                public void onFailure(Call<MediaFeed> call, Throwable throwable) {
                    EventBus.getDefault().post(new UpdateNotifier(
                            SongsFragment.class.getSimpleName(), genre.getNumber(), false));
                }
            });
        }
    }
}
