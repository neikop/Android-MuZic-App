package com.example.windzlord.muzic;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.example.windzlord.muzic.managers.MusicPlayer;
import com.example.windzlord.muzic.managers.NetworkManager;
import com.example.windzlord.muzic.managers.PreferenceManager;
import com.example.windzlord.muzic.managers.RealmManager;
import com.example.windzlord.muzic.models.Genre;
import com.example.windzlord.muzic.models.Song;
import com.example.windzlord.muzic.models.api_models.MediaFeed;
import com.example.windzlord.muzic.notifiers.UpdateNotifier;
import com.example.windzlord.muzic.screens.SongFragment;
import com.example.windzlord.muzic.services.MusicService;
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

        if (RealmManager.getInstance().getAliveGenres().isEmpty()) goGenre();
        goTopSong();
    }

    private void goGenre() {
        System.out.println("goGenre");

        InputStream inputStream = getResources().openRawResource(R.raw.genre);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            while (bufferedReader.readLine() != null)
                RealmManager.getInstance().addGenre(Genre.createGenre(bufferedReader.readLine()));

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (IOException e) {
            for (String genre : PreferenceManager.getInstance().getGenres())
                RealmManager.getInstance().addGenre(Genre.createGenre(genre));
        }
    }

    private void goTopSong() {
        System.out.println("Service goTopSong");
        RealmManager.getInstance().clearTopSong();

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.TOP_SONG_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);

        for (Genre genre : RealmManager.getInstance().getAliveGenres()) {

            musicService.getMediaFeed(genre.getGenreID()).enqueue(new Callback<MediaFeed>() {
                @Override
                public void onResponse(Call<MediaFeed> call, Response<MediaFeed> response) {
                    for (MediaFeed.Feed.Entry entry : response.body().getTopSongList()) {
                        RealmManager.getInstance().addSong(Song.create(genre.getGenreID(), entry));
                    }
                    EventBus.getDefault().post(new UpdateNotifier(
                            SongFragment.class.getSimpleName(), genre.getGenreID(), true));
                }

                @Override
                public void onFailure(Call<MediaFeed> call, Throwable throwable) {
                    EventBus.getDefault().post(new UpdateNotifier(
                            SongFragment.class.getSimpleName(), genre.getGenreID(), false));
                }
            });
        }
    }
}
