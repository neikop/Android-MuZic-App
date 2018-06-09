package com.example.windzlord.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.windzlord.muzic.Logistic;
import com.example.windzlord.muzic.MainActivity;
import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.adapters.SongAdapter;
import com.example.windzlord.muzic.adapters.listeners.RecyclerViewListener;
import com.example.windzlord.muzic.managers.RealmManager;
import com.example.windzlord.muzic.models.Genre;
import com.example.windzlord.muzic.models.Song;
import com.example.windzlord.muzic.models.api_models.MediaFeed;
import com.example.windzlord.muzic.notifiers.SongChanger;
import com.example.windzlord.muzic.notifiers.UpdateNotifier;
import com.example.windzlord.muzic.notifiers.WaitingChanger;
import com.example.windzlord.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongFragment extends Fragment {

    @BindView(R.id.image_genre_songs)
    ImageView imageGenreSongs;

    @BindView(R.id.text_genre_name)
    TextView textGenreName;

    @BindView(R.id.recycler_view_songs)
    RecyclerView recyclerViewSongs;

    @BindView(R.id.progress_bar_waiting)
    ProgressBar waitingBar;

    @BindView(R.id.button_refresh)
    RelativeLayout buttonRefresh;

    private Genre genre;
    private boolean isWaiting;

    public SongFragment() {
        // Required empty public constructor
    }

    public void setGenreIndex(int index) {
        genre = RealmManager.getInstance().getAliveGenres().get(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        goContent();
        if (RealmManager.getInstance().getSongs(genre.getGenreID()).isEmpty()) {
            goUpdate();
        } else goTopSong();
    }

    private void goContent() {
        Logistic.setImagePicture(getActivity(), imageGenreSongs, "images/genre_" + genre.getGenreID() + ".png");
        textGenreName.setText(genre.getName().toUpperCase());

        isWaiting = false;
        waitingBar.setVisibility(View.INVISIBLE);
        buttonRefresh.setVisibility(View.INVISIBLE);
    }

    private void goTopSong() {
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewSongs.setAdapter(new SongAdapter(
                RealmManager.getInstance().getSongs(genre.getGenreID())));
        recyclerViewSongs.getAdapter().notifyDataSetChanged();

        recyclerViewSongs.addOnItemTouchListener(new RecyclerViewListener(
                getActivity(), recyclerViewSongs, new RecyclerViewListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (isWaiting) return;
                EventBus.getDefault().post(new SongChanger(
                        MainActivity.class.getSimpleName(), genre, position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @OnClick(R.id.button_play)
    public void onPlayPressed() {
        if (isWaiting) return;
        EventBus.getDefault().post(new SongChanger(
                MainActivity.class.getSimpleName(), genre, 0));
    }

    @OnClick(R.id.image_button_back)
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
    public void goUpdate(UpdateNotifier event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;
        if (!genre.getGenreID().equals(event.getGenreID())) return;

        waitingBar.setVisibility(View.INVISIBLE);
        if (!event.isSuccess()) {
            Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
            buttonRefresh.setVisibility(View.VISIBLE);
        } else goTopSong();
    }

    @OnClick(R.id.button_refresh)
    public void goUpdate() {
        System.out.println("goUpdate");
        buttonRefresh.setVisibility(View.INVISIBLE);
        waitingBar.setVisibility(View.VISIBLE);
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

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
