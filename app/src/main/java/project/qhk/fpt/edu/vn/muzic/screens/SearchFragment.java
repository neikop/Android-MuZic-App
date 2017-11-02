package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.SongAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.models.api_models.SearchResult;
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
        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SEARCH__API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);

        musicService.getSearchResult(Logistic.CLIENT_ID, 10, search).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                SearchResult result = response.body();

                RealmManager.getInstance().clearSearch();

                Genre genre = new Genre("SEARCH");
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

            }
        });
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
