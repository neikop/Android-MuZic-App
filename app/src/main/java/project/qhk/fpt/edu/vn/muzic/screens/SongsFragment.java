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
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.SongAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.objects.SongChanger;
import project.qhk.fpt.edu.vn.muzic.objects.WaitingChanger;

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

    private Genre genre;
    private boolean waiting;

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
        goTopSong();
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

        waiting = false;
        waitingBar.setVisibility(View.INVISIBLE);
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
                if (waiting) return;
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
        if (waiting) return;
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
        waiting = event.isWaiting();
        waitingBar.setVisibility(waiting ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
