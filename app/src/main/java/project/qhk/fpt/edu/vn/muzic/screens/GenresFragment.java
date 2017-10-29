package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.GenreAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.objects.FragmentChanger;
import project.qhk.fpt.edu.vn.muzic.objects.Notifier;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment {

    @BindView(R.id.recycler_view_genres)
    RecyclerView recyclerViewGenres;

    public GenresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);

        goContent();
    }

    private void goContent() {

        GridLayoutManager manager = new GridLayoutManager(
                getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 0 ? 2 : 1;
            }
        });
        recyclerViewGenres.setLayoutManager(manager);
        recyclerViewGenres.setAdapter(new GenreAdapter());

        recyclerViewGenres.addOnItemTouchListener(new RecyclerViewListener(
                getActivity(), recyclerViewGenres, new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SongsFragment songsFragment = new SongsFragment();
                songsFragment.setPosition(position);
                EventBus.getDefault().post(new FragmentChanger(
                        GenresFragment.this.getClass().getSimpleName(), songsFragment, true));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

}
