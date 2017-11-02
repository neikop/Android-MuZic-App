package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.SongAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.notifiers.SongChanger;
import project.qhk.fpt.edu.vn.muzic.notifiers.WaitingChanger;

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

    private Genre playlist;
    private List<Song> songList;

    private boolean isWaiting;

    public FavourSongFragment() {
        // Required empty public constructor
    }

    public void setPlaylistIndex(int index) {
        playlist = RealmManager.getInstance().getAlivePlaylist().get(index);
        songList = RealmManager.getInstance().getSongs(playlist.getGenreID());
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

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
