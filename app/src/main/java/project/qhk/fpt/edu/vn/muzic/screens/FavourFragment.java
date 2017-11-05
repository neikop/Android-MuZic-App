package project.qhk.fpt.edu.vn.muzic.screens;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.PlaylistAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.managers.NetworkManager;
import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.api_models.Result;
import project.qhk.fpt.edu.vn.muzic.notifiers.FragmentChanger;
import project.qhk.fpt.edu.vn.muzic.notifiers.SimpleNotifier;
import project.qhk.fpt.edu.vn.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavourFragment extends Fragment {

    @BindView(R.id.recycler_view_playlist)
    RecyclerView recyclerViewPlaylist;

    @BindView(R.id.login_bar_waiting)
    ProgressBar waitingBar;

    public FavourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favour, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        goContent();
    }

    private void goContent() {
        waitingBar.setVisibility(View.INVISIBLE);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPlaylist.setAdapter(new PlaylistAdapter());
        recyclerViewPlaylist.getAdapter().notifyDataSetChanged();

        recyclerViewPlaylist.addOnItemTouchListener(new RecyclerViewListener(
                getActivity(), recyclerViewPlaylist, new RecyclerViewListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                FavourSongFragment songFragment = new FavourSongFragment();
                songFragment.setPlaylistIndex(position);
                EventBus.getDefault().post(new FragmentChanger(
                        FavourFragment.this.getClass().getSimpleName(), songFragment, true));
            }

            @Override
            public void onLongItemClick(View view, int position) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.menu_favour, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if ("Remove".equals(item.getTitle())) {
                            RealmManager.getInstance().removePlaylist(RealmManager.getInstance().getAllPlaylist().get(position));
                            if (NetworkManager.getInstance().isConnectedToInternet() && !PreferenceManager.getInstance().getToken().isEmpty()){
                                waitingBar.setVisibility(View.VISIBLE);
                                JsonObject object = new JsonObject();
                                object.addProperty("playlistId", RealmManager.getInstance().getAllPlaylist().get(position).get_id());
                                object.addProperty("token", PreferenceManager.getInstance().getToken());
                                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

                                Retrofit mediaRetrofit = new Retrofit.Builder()
                                        .baseUrl(Logistic.SERVER_API)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                MusicService musicService = mediaRetrofit.create(MusicService.class);
                                musicService.removePlaylist(body).enqueue(new Callback<Result>() {
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
                            recyclerViewPlaylist.getAdapter().notifyDataSetChanged();
                        }
                        if ("Rename".equals(item.getTitle())) {
                            EditText input = new EditText(getContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Name");
                            builder.setView(input);
                            builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = input.getText().toString().trim();
                                    if (name.isEmpty()) name = "Empty name";

                                    RealmManager.getInstance().renamePlaylist(RealmManager.getInstance().getAllPlaylist().get(position), name);

                                    if (NetworkManager.getInstance().isConnectedToInternet()  && !PreferenceManager.getInstance().getToken().isEmpty()){
                                        waitingBar.setVisibility(View.VISIBLE);
                                        JsonObject object = new JsonObject();
                                        object.addProperty("playlistId", RealmManager.getInstance().getAllPlaylist().get(position).get_id());
                                        object.addProperty("playlistName", name);
                                        object.addProperty("token", PreferenceManager.getInstance().getToken());

                                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

                                        Retrofit mediaRetrofit = new Retrofit.Builder()
                                                .baseUrl(Logistic.SERVER_API)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        MusicService musicService = mediaRetrofit.create(MusicService.class);
                                        musicService.renamePlaylist(body).enqueue(new Callback<Result>() {
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

                                    recyclerViewPlaylist.getAdapter().notifyDataSetChanged();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        }));
    }

    @Subscribe
    public void onUpdatePlaylist(SimpleNotifier event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;

        recyclerViewPlaylist.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
