package com.example.windzlord.muzic.screens;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import com.example.windzlord.muzic.Logistic;
import com.example.windzlord.muzic.MainActivity;
import com.example.windzlord.muzic.R;
import com.example.windzlord.muzic.managers.PreferenceManager;
import com.example.windzlord.muzic.managers.RealmManager;
import com.example.windzlord.muzic.models.Playlist;
import com.example.windzlord.muzic.models.Song;
import com.example.windzlord.muzic.models.api_models.LocalSyncJSON;
import com.example.windzlord.muzic.models.api_models.LoginResult;
import com.example.windzlord.muzic.models.api_models.PlaylistResult;
import com.example.windzlord.muzic.models.api_models.Result;
import com.example.windzlord.muzic.notifiers.SimpleNotifier;
import com.example.windzlord.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.layout_login)
    ViewGroup layoutLogin;

    @BindView(R.id.login_username)
    EditText loginUsername;

    @BindView(R.id.login_password)
    EditText loginPassword;

    @BindView(R.id.layout_register)
    ViewGroup layoutRegister;

    @BindView(R.id.register_nickname)
    EditText registerNickname;

    @BindView(R.id.register_email)
    EditText registerEmail;

    @BindView(R.id.register_username)
    EditText registerUsername;

    @BindView(R.id.register_password)
    EditText registerPassword;

    @BindView(R.id.layout_profile)
    ViewGroup layoutProfile;

    @BindView(R.id.text_nickname)
    TextView textNickname;

    @BindView(R.id.image_sign)
    ImageView imageSign;

    @BindView(R.id.text_sign)
    TextView textSign;

    @BindView(R.id.login_bar_waiting)
    ProgressBar waitingBar;

    private boolean isWaiting = false;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);

        getContent();
        new CountDownTimer(500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                ((MainActivity) getActivity()).setLayoutDaddy(View.INVISIBLE);
            }
        }.start();
    }

    private void getContent() {
        waitingBar.setVisibility(View.INVISIBLE);
        layoutLogin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.GONE);
        layoutProfile.setVisibility(View.GONE);

        if (PreferenceManager.getInstance().isLogin()) {
            imageSign.setImageResource(R.drawable.icon_logout);
            textSign.setText("Logout");

            layoutProfile.setVisibility(View.VISIBLE);
            textNickname.setText(PreferenceManager.getInstance().getUsername());
        } else {
            imageSign.setImageResource(R.drawable.icon_register);
            textSign.setText("Register");

            layoutLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setLayoutDaddy(View.VISIBLE);
    }

    @OnClick(R.id.login_button_back)
    public void onBackPressed() {
        if (isWaiting) return;
        getActivity().onBackPressed();
    }

    @OnClick(R.id.text_sign)
    public void goCheckSign() {
        if (isWaiting) return;

        layoutLogin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.GONE);
        layoutProfile.setVisibility(View.GONE);

        if ("Login".equalsIgnoreCase(textSign.getText().toString())) {
            layoutLogin.setVisibility(View.VISIBLE);
            imageSign.setImageResource(R.drawable.icon_register);
            textSign.setText("Register");
            loginUsername.setText("");
            loginPassword.setText("");
        } else if ("Register".equalsIgnoreCase(textSign.getText().toString())) {
            layoutRegister.setVisibility(View.VISIBLE);
            imageSign.setImageResource(R.drawable.icon_login);
            textSign.setText("Login");
            registerUsername.setText("");
            registerPassword.setText("");
            registerNickname.setText("");
            registerEmail.setText("");
        } else if ("Logout".equalsIgnoreCase(textSign.getText().toString())) {
            layoutProfile.setVisibility(View.VISIBLE);
            onLogoutButton();
        }
    }

    @OnClick(R.id.image_sign)
    public void goCheckImageSign() {
        goCheckSign();
    }

    private void onLogoutButton() {
        if (isWaiting) return;
        isWaiting = true;

        System.out.println("goLogout");
        waitingBar.setVisibility(View.VISIBLE);

        new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;

                layoutProfile.setVisibility(View.GONE);
                layoutRegister.setVisibility(View.GONE);

                imageSign.setImageResource(R.drawable.icon_register);
                textSign.setText("Register");

                layoutLogin.setVisibility(View.VISIBLE);
                loginUsername.setText("");
                loginPassword.setText("");

                PreferenceManager.getInstance().goLogout();
            }
        }.start();
    }

    @OnClick(R.id.login_button)
    public void onLoginButton() {
        if (isWaiting) return;
        isWaiting = true;

        if (loginUsername.getText().toString().isEmpty()
                || loginPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "PLEASE FILL ALL FIELDS", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("goLogin");
        waitingBar.setVisibility(View.VISIBLE);

        JsonObject object = new JsonObject();
        object.addProperty("username", loginUsername.getText().toString());
        object.addProperty("password", loginPassword.getText().toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getLoginResult(body).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;

                LoginResult result = response.body();
                if (result == null) {
                    Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

                if (result.isSuccess()) {
                    PreferenceManager.getInstance().goLogin(result);
                    layoutLogin.setVisibility(View.GONE);
                    layoutRegister.setVisibility(View.GONE);

                    imageSign.setImageResource(R.drawable.icon_logout);
                    textSign.setText("Logout");

                    layoutProfile.setVisibility(View.VISIBLE);
                    textNickname.setText(PreferenceManager.getInstance().getUsername());

                    goTaiHard();
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable throwable) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;
                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.register_button)
    public void onRegisterButton() {
        if (isWaiting) return;
        isWaiting = true;

        if (registerNickname.getText().toString().isEmpty()
                || registerEmail.getText().toString().isEmpty()
                || registerUsername.getText().toString().isEmpty()
                || registerPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "PLEASE FILL ALL FIELDS", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("goRegister");
        waitingBar.setVisibility(View.VISIBLE);

        JsonObject object = new JsonObject();
        object.addProperty("username", registerUsername.getText().toString());
        object.addProperty("password", registerPassword.getText().toString());
        object.addProperty("name", registerNickname.getText().toString());
        object.addProperty("email", registerEmail.getText().toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getRegisterResult(body).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;

                Result result = response.body();
                if (result == null) {
                    Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

                if (result.isSuccess()) {
                    layoutRegister.setVisibility(View.GONE);
                    layoutLogin.setVisibility(View.VISIBLE);
                    imageSign.setImageResource(R.drawable.icon_register);
                    textSign.setText("Register");
                    loginUsername.setText("");
                    loginPassword.setText("");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable throwable) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;
                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goTaiHard() {
        if (isWaiting) return;
        isWaiting = true;
        waitingBar.setVisibility(View.VISIBLE);

        if (RealmManager.getInstance().getAllPlaylist().isEmpty())
            getPlaylistByUser();
        else syncPlaylist();

    }

    private void getPlaylistByUser() {
        JsonObject object = new JsonObject();
        object.addProperty("token", PreferenceManager.getInstance().getToken());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getPlaylistByUser(body).enqueue(new Callback<PlaylistResult>() {
            @Override
            public void onResponse(Call<PlaylistResult> call, Response<PlaylistResult> response) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;

                PlaylistResult result = response.body();
                if (result == null) {
                    return;
                }

                if (result.isSuccess()) {
                    RealmManager.getInstance().clearSongsPlaylist();
                    RealmManager.getInstance().clearAllPlaylist();

                    for (PlaylistResult.Playlist returnPlaylist : result.getPlaylist()) {
                        Playlist playlist = Playlist.createPlaylist(returnPlaylist.getName());
                        playlist.set_id(returnPlaylist.getId());
                        RealmManager.getInstance().addNewPlaylist(playlist);

                        for (PlaylistResult.Playlist.Song returnSong : returnPlaylist.getSongList()) {
                            Song song = Song.createForPlaylist(playlist.getPlaylistID(), new Song(returnSong));
                            RealmManager.getInstance().addSong(song);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaylistResult> call, Throwable throwable) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;
            }
        });
    }

    private void syncPlaylist() {
        LocalSyncJSON localSyncJSON = new LocalSyncJSON();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String requestJSON = gson.toJson(localSyncJSON, localSyncJSON.getClass());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJSON);

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.syncPlaylist(body).enqueue(new Callback<PlaylistResult>() {
            @Override
            public void onResponse(Call<PlaylistResult> call, Response<PlaylistResult> response) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;

                PlaylistResult result = response.body();
                if (result == null) {
                    return;
                }

                if (result.isSuccess()) {
                    RealmManager.getInstance().clearSongsPlaylist();
                    RealmManager.getInstance().clearAllPlaylist();

                    for (PlaylistResult.Playlist returnPlaylist : result.getPlaylist()) {
                        Playlist playlist = Playlist.createPlaylist(returnPlaylist.getName());
                        playlist.set_id(returnPlaylist.getId());
                        RealmManager.getInstance().addNewPlaylist(playlist);

                        for (PlaylistResult.Playlist.Song returnSong : returnPlaylist.getSongList()) {
                            Song song = Song.createForPlaylist(playlist.getPlaylistID(), new Song(returnSong));
                            RealmManager.getInstance().addSong(song);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaylistResult> call, Throwable t) {
                waitingBar.setVisibility(View.INVISIBLE);
                isWaiting = false;
            }
        });
    }
}
