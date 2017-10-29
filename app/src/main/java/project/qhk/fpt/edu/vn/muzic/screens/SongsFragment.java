package project.qhk.fpt.edu.vn.muzic.screens;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;

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

    private Genre genre;

    public void setPosition(int position) {
        genre = RealmManager.getInstance().getGenres().get(position);
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
    }

    private void goTopSong() {
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewSongs.setAdapter(new SongAdapter(
                RealmManager.getInstance().getTopSong(media.getId())));
        recyclerViewSongs.getAdapter().notifyDataSetChanged();

        recyclerViewSongs.addOnItemTouchListener(new RecyclerViewListener(
                getActivity(), recyclerViewSongs, new RecyclerViewListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                EventBus.getDefault().post(new MediaChanger(
                        MainActivity.class.getSimpleName(), media.getIndex(), position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

}
