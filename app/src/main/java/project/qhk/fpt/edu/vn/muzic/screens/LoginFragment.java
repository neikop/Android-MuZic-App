package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.models.api_models.LoginResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.Result;
import project.qhk.fpt.edu.vn.muzic.notifiers.FragmentChanger;
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

    @BindView(R.id.register_username)
    EditText register_username;

    @BindView(R.id.register_password)
    EditText register_password;

    @BindView(R.id.register_fullname)
    EditText register_fullname;

    @BindView(R.id.register_email)
    EditText register_email;

    @BindView(R.id.register_retype_password)
    EditText register_retype_password;

    @BindView(R.id.login_bar_waiting)
    ProgressBar waitingBar;

    @BindView(R.id.register_layout)
    LinearLayout register_layout;

    @BindView(R.id.signin_layout)
    LinearLayout signin_layout;

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
    }

    @OnClick(R.id.login_button_back)
    public void onBackPressed() {
        ((MainActivity) getActivity()).setLayoutDaddy(View.VISIBLE);
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
                    PreferenceManager.getInstance().login(result.getName(), result.getToken(), result.getUser().getName(), result.getUser().getEmail());
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

    @OnClick(R.id.register_button)
    public void onRegisterButton(){
        System.out.println("goRegister");
        register_layout.setVisibility(View.VISIBLE);
        signin_layout.setVisibility(View.INVISIBLE);
        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    @OnClick(R.id.register_button_back)
    public void onRegisterButtonBack(){
        System.out.println("goRegisterBack");
        register_layout.setVisibility(View.INVISIBLE);
        signin_layout.setVisibility(View.VISIBLE);
        register_email.setText("");
        register_password.setText("");
        register_fullname.setText("");
        register_username.setText("");
        register_retype_password.setText("");
    }

    @OnClick(R.id.create_account_button)
    public void onCreateAccountButton(){
        System.out.println("goCreateAccount");
        if (!register_password.getText().toString().equals(register_retype_password.getText().toString())){
            Toast.makeText(getContext(), "Please input correct Retype Password", Toast.LENGTH_SHORT).show();
            return;
        }
        waitingBar.setVisibility(View.VISIBLE);
        JsonObject object = new JsonObject();
        object.addProperty("username", register_username.getText().toString());
        object.addProperty("password", register_password.getText().toString());
        object.addProperty("name", register_fullname.getText().toString());
        object.addProperty("email", register_email.getText().toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getRegisterResult(body).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                System.out.println(result.toString());
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                if (result.isSuccess()) {
                    onRegisterButtonBack();
                }
                waitingBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                waitingBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
