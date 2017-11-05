package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.SongAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.managers.NetworkManager;
import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Playlist;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.models.api_models.Result;
import project.qhk.fpt.edu.vn.muzic.notifiers.SimpleNotifier;
import project.qhk.fpt.edu.vn.muzic.notifiers.SongChanger;
import project.qhk.fpt.edu.vn.muzic.notifiers.WaitingChanger;
import project.qhk.fpt.edu.vn.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavourSongFragment extends Fragment {

    @BindView(R.id.text_favour_name)
    TextView textFavourName;

    @BindView(R.id.text_favour_size)
    TextView textFavourSize;

    @BindView(R.id.recycler_view_favour_songs)
    RecyclerView recyclerViewSongs;

    @BindView(R.id.progress_bar_favour_waiting)
    ProgressBar waitingBar;

    private Playlist playlist;
    private List<Song> songList;

    private boolean isWaiting;

    public FavourSongFragment() {
        // Required empty public constructor
    }

    public void setPlaylistIndex(int index) {
        playlist = RealmManager.getInstance().getAllPlaylist().get(index);
        songList = RealmManager.getInstance().getSongsOfPlaylist(playlist.getPlaylistID());
        System.out.println("playlist id : "+playlist.getPlaylistID());
        System.out.println("songlist size: "+songList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favour_song, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        goContent();
        goFavourSong();
    }

    private void goContent() {
        textFavourName.setText(playlist.getName());
        textFavourSize.setText(songList.size() + " songs");

        isWaiting = false;
        waitingBar.setVisibility(View.INVISIBLE);
    }

    private void goFavourSong() {
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewSongs.setAdapter(new SongAdapter(songList));
        recyclerViewSongs.getAdapter().notifyDataSetChanged();

        recyclerViewSongs.addOnItemTouchListener(new RecyclerViewListener(
                getActivity(), recyclerViewSongs, new RecyclerViewListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (isWaiting) return;
                EventBus.getDefault().post(new SongChanger(
                        MainActivity.class.getSimpleName(), playlist, position));
            }

            @Override
            public void onLongItemClick(View view, int position) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.menu_favour_song, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if ("Remove".equals(item.getTitle())) {

                            if (NetworkManager.getInstance().isConnectedToInternet()  && !PreferenceManager.getInstance().getToken().isEmpty()){
                                waitingBar.setVisibility(View.VISIBLE);
                                JsonObject object = new JsonObject();
                                object.addProperty("playlistId", playlist.get_id());
                                object.addProperty("songId", songList.get(position).get_id());
                                object.addProperty("token", PreferenceManager.getInstance().getToken());

                                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

                                Retrofit mediaRetrofit = new Retrofit.Builder()
                                        .baseUrl(Logistic.SERVER_API)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                MusicService musicService = mediaRetrofit.create(MusicService.class);
                                musicService.removeFromPlaylist(body).enqueue(new Callback<Result>() {
                                    @Override
                                    public void onResponse(Call<Result> call, Response<Result> response) {
                                        Result result = response.body();
                                        Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                                        waitingBar.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onFailure(Call<Result> call, Throwable t) {
                                        Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                                        waitingBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }

                            RealmManager.getInstance().removeFavourSong(songList.get(position));
                            songList = RealmManager.getInstance().getSongs(playlist.getPlaylistID());
                            recyclerViewSongs.getAdapter().notifyDataSetChanged();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        }));
    }

    @OnClick(R.id.button_favour_play)
    public void onPlayPressed() {
        if (isWaiting) return;
        EventBus.getDefault().post(new SongChanger(
                MainActivity.class.getSimpleName(), playlist, 0));
    }

    @OnClick(R.id.button_favour_back)
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    @Subscribe
    public void changeWaiting(WaitingChanger event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;

        isWaiting = event.isWaiting();
        waitingBar.setVisibility(isWaiting ? View.VISIBLE : View.INVISIBLE);
    }

    @Subscribe
    public void onUpdatePlaylist(SimpleNotifier event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;

        recyclerViewSongs.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
