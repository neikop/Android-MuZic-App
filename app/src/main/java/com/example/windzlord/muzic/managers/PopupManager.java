package com.example.windzlord.muzic.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import com.example.windzlord.muzic.Logistic;
import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.models.Playlist;
import com.example.windzlord.muzic.models.Song;
import com.example.windzlord.muzic.models.api_models.AddToPlaylistResult;
import com.example.windzlord.muzic.models.api_models.LocalAddJSON;
import com.example.windzlord.muzic.notifiers.SimpleNotifier;
import com.example.windzlord.muzic.screens.FavourFragment;
import com.example.windzlord.muzic.screens.FavourSongFragment;
import com.example.windzlord.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by WindzLord on 11/1/2017.
 */

public class PopupManager {

    private static PopupManager instance;
    private PopupMenu popup;

    private PopupManager(Context context, View view) {
        popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.menu_like, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == -1)
                    createPlaylist(context);
                else addToPlaylist(context, item.getItemId());

                EventBus.getDefault().post(new SimpleNotifier(FavourSongFragment.class.getSimpleName()));
                EventBus.getDefault().post(new SimpleNotifier(FavourFragment.class.getSimpleName()));

                return true;
            }
        });
    }

    public static PopupManager getInstance() {
        return instance;
    }

    public static void init(Context context, View view) {
        instance = new PopupManager(context, view);
    }

    private void createPlaylist(Context context) {
        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Name");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = input.getText().toString().trim();
                if (title.isEmpty()) title = "Empty name";

                Playlist playlist = Playlist.createPlaylist(title);
                Song addedSong = Song.createForPlaylist(playlist.getPlaylistID(), MusicPlayer.getInstance().getSong());

                if (NetworkManager.getInstance().isConnectedToInternet() && !PreferenceManager.getInstance().getToken().isEmpty()) {
                    LocalAddJSON localAddJSON = new LocalAddJSON(playlist.get_id() == null ? "" : playlist.get_id(), title, addedSong);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String requestJSON = gson.toJson(localAddJSON, localAddJSON.getClass());
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJSON);

                    Retrofit mediaRetrofit = new Retrofit.Builder()
                            .baseUrl(Logistic.SERVER_API)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    MusicService musicService = mediaRetrofit.create(MusicService.class);
                    musicService.addToPlaylist(body).enqueue(new Callback<AddToPlaylistResult>() {
                        @Override
                        public void onResponse(Call<AddToPlaylistResult> call, Response<AddToPlaylistResult> response) {
                            AddToPlaylistResult result = response.body();
                            if (result == null) {
                                RealmManager.getInstance().addNewPlaylist(playlist);
                                RealmManager.getInstance().addSong(addedSong);
                                return;
                            }
                            if (result.isSuccess()) {
                                playlist.set_id(result.getPlaylistId());
                                addedSong.set_id(result.getSongId());
                                RealmManager.getInstance().addNewPlaylist(playlist);
                                RealmManager.getInstance().addSong(addedSong);
                            }
                        }

                        @Override
                        public void onFailure(Call<AddToPlaylistResult> call, Throwable t) {
                            RealmManager.getInstance().addNewPlaylist(playlist);
                            RealmManager.getInstance().addSong(addedSong);
                        }
                    });
                } else {
                    RealmManager.getInstance().addNewPlaylist(playlist);
                    RealmManager.getInstance().addSong(addedSong);
                }

                Toast.makeText(context, "Add to " + title, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addToPlaylist(Context context, int indexList) {
        Playlist playlist = RealmManager.getInstance().getAllPlaylist().get(indexList);
        Song addedSong = Song.createForPlaylist(playlist.getPlaylistID(), MusicPlayer.getInstance().getSong());

        if (PreferenceManager.getInstance().isLogin()) {
            LocalAddJSON localAddJSON = new LocalAddJSON(playlist.get_id() == null ? "" : playlist.get_id(), playlist.getName(), addedSong);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String requestJSON = gson.toJson(localAddJSON, localAddJSON.getClass());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJSON);

            Retrofit mediaRetrofit = new Retrofit.Builder()
                    .baseUrl(Logistic.SERVER_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MusicService musicService = mediaRetrofit.create(MusicService.class);
            musicService.addToPlaylist(body).enqueue(new Callback<AddToPlaylistResult>() {
                @Override
                public void onResponse(Call<AddToPlaylistResult> call, Response<AddToPlaylistResult> response) {
                    AddToPlaylistResult result = response.body();
                    if (result == null) {
                        RealmManager.getInstance().addSong(addedSong);
                        return;
                    }
                    if (result.isSuccess()) {
                        addedSong.set_id(result.getSongId());
                        RealmManager.getInstance().addSong(addedSong);
                    }
                }

                @Override
                public void onFailure(Call<AddToPlaylistResult> call, Throwable t) {
                    RealmManager.getInstance().addSong(addedSong);
                }
            });
        } else {
            RealmManager.getInstance().addSong(addedSong);
        }

        Toast.makeText(context, "Add to " + playlist.getName(), Toast.LENGTH_SHORT).show();
    }

    public void show() {
        popup.getMenu().clear();
        int index = -1;
        addItem(index++);
        for (Playlist playlist : RealmManager.getInstance().getAllPlaylist()) {
            addItem(index++, playlist.getName());
        }
        popup.show();
    }

    public void addItem(int index) {
        popup.getMenu().add(0, index, 0, "New playlist");
    }

    public void addItem(int index, String title) {
        popup.getMenu().add(0, index, 0, title);
    }
}
