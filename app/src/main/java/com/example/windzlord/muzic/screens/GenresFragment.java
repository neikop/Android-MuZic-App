package com.example.windzlord.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.adapters.GenreAdapter;
import com.example.windzlord.muzic.adapters.listeners.RecyclerViewListener;
import com.example.windzlord.muzic.notifiers.FragmentChanger;

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
                SongFragment songFragment = new SongFragment();
                songFragment.setGenreIndex(position);
                EventBus.getDefault().post(new FragmentChanger(
                        GenresFragment.this.getClass().getSimpleName(), songFragment, true));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

}
