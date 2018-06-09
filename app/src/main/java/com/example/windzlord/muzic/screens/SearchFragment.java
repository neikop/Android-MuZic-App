package com.example.windzlord.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

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
import com.example.windzlord.muzic.models.api_models.SearchResult;
import com.example.windzlord.muzic.notifiers.SongChanger;
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
public class SearchFragment extends Fragment {

    @BindView(R.id.text_search)
    EditText textSearch;

    @BindView(R.id.recycler_view_search)
    RecyclerView recyclerViewSearch;

    @BindView(R.id.search_bar_waiting)
    ProgressBar waitingBar;

    private boolean isWaiting;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        goContext();
    }

    private void goContext() {
        isWaiting = false;
        waitingBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.button_search)
    public void goSearch() {
        if (isWaiting) return;

        String search = textSearch.getText().toString();
        System.out.println("searching: " + search);

        changeWaiting(true);

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SEARCH__API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);

        musicService.getSearchResult(Logistic.CLIENT_ID, 10, search).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                changeWaiting(false);

                RealmManager.getInstance().clearSearch();
                Genre genre = new Genre("SEARCH");

                SearchResult result = response.body();
                for (SearchResult.SearchSong searchSong : result.getResults())
                    RealmManager.getInstance().addSong(
                            Song.create("SEARCH", new Song(searchSong)));

                List<Song> topSongList = RealmManager.getInstance().getSongs("SEARCH");

                recyclerViewSearch.setLayoutManager(new LinearLayoutManager(
                        getActivity(), LinearLayoutManager.VERTICAL, false));
                recyclerViewSearch.setAdapter(new SongAdapter(topSongList));
                recyclerViewSearch.getAdapter().notifyDataSetChanged();

                recyclerViewSearch.addOnItemTouchListener(new RecyclerViewListener(
                        getActivity(), recyclerViewSearch, new RecyclerViewListener.OnItemClickListener() {

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

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                changeWaiting(false);
            }
        });
    }

    private void changeWaiting(boolean isWaiting) {
        this.isWaiting = isWaiting;
        waitingBar.setVisibility(isWaiting ? View.VISIBLE : View.INVISIBLE);
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
