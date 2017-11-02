package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.models.api_models.LoginResult;
import project.qhk.fpt.edu.vn.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.login_username)
    EditText editTextUsername;

    @BindView(R.id.login_password)
    EditText editTextPassword;

    @BindView(R.id.login_bar_waiting)
    ProgressBar waitingBar;

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
    }

    private void getContent() {
        waitingBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.login_button_back)
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.login_button)
    public void onLoginButton() {
        System.out.println("goLogin");
        waitingBar.setVisibility(View.VISIBLE);

        JsonObject object = new JsonObject();
        object.addProperty("username", editTextUsername.getText().toString());
        object.addProperty("password", editTextPassword.getText().toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getLoginResult(body).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                LoginResult result = response.body();
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                if (result.isSuccess()) {
                    PreferenceManager.getInstance().login(result.getName(), result.getToken());
                    onBackPressed();
                }
                waitingBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable throwable) {
                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                waitingBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
