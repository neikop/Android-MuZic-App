package project.qhk.fpt.edu.vn.muzic.screens;


import android.graphics.drawable.Drawable;
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

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.qhk.fpt.edu.vn.muzic.Constant;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.MainApplication;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.SongAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.managers.NetworkManager;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.models.api_models.MediaFeed;
import project.qhk.fpt.edu.vn.muzic.objects.SimpleNotifier;
import project.qhk.fpt.edu.vn.muzic.objects.SongChanger;
import project.qhk.fpt.edu.vn.muzic.objects.UpdateNotifier;
import project.qhk.fpt.edu.vn.muzic.objects.WaitingChanger;
import project.qhk.fpt.edu.vn.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    @BindView(R.id.image_genre_songs)
    ImageView imageGenreSongs;

    @BindView(R.id.text_genre_songs)
    TextView textGenreSongs;

    @BindView(R.id.recycler_view_songs)
    RecyclerView recyclerViewSongs;

    @BindView(R.id.progress_bar_waiting)
    ProgressBar waitingBar;

    @BindView(R.id.button_refresh)
    RelativeLayout buttonRefresh;

    private Genre genre;
    private boolean isWaiting;

    public void getGenreIndex(int index) {
        genre = RealmManager.getInstance().getGenres().get(index);
    }

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        goContent();
        if (RealmManager.getInstance().getSongs(genre.getNumber()).isEmpty()) {
            goUpdate();
        } else goTopSong();
    }

    private void goContent() {
        try {
            InputStream stream = getActivity().getAssets().open("images/genre_" + genre.getNumber() + ".png");
            Drawable drawable = Drawable.createFromStream(stream, null);
            imageGenreSongs.setImageDrawable(drawable);
            stream.close();
        } catch (IOException ex) {

        }
        textGenreSongs.setText(genre.getName().toUpperCase());

        isWaiting = false;
        waitingBar.setVisibility(View.INVISIBLE);
        buttonRefresh.setVisibility(View.INVISIBLE);
    }

    private void goTopSong() {
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewSongs.setAdapter(new SongAdapter(
                RealmManager.getInstance().getSongs(genre.getNumber())));
        recyclerViewSongs.getAdapter().notifyDataSetChanged();

        recyclerViewSongs.addOnItemTouchListener(new RecyclerViewListener(
                getActivity(), recyclerViewSongs, new RecyclerViewListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (isWaiting) return;
                EventBus.getDefault().post(new SongChanger(
                        MainActivity.class.getSimpleName(), genre.getIndex(), position));
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
                MainActivity.class.getSimpleName(), genre.getIndex(), 0));
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
        if (!genre.getNumber().equals(event.getNumber())) return;

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

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
