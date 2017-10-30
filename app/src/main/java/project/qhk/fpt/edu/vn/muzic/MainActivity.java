package project.qhk.fpt.edu.vn.muzic;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.qhk.fpt.edu.vn.muzic.managers.MusicPlayer;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.models.api_models.SongMp3;
import project.qhk.fpt.edu.vn.muzic.objects.FragmentChanger;
import project.qhk.fpt.edu.vn.muzic.objects.Notifier;
import project.qhk.fpt.edu.vn.muzic.objects.SongChanger;
import project.qhk.fpt.edu.vn.muzic.objects.WaitingChanger;
import project.qhk.fpt.edu.vn.muzic.screens.GenresFragment;
import project.qhk.fpt.edu.vn.muzic.screens.MainActivityFragment;
import project.qhk.fpt.edu.vn.muzic.screens.SongsFragment;
import project.qhk.fpt.edu.vn.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.layout_daddy)
    LinearLayout layoutDaddy;

    @BindView(R.id.cute_player)
    RelativeLayout cutePlayer;

    @BindView(R.id.cute_seek_bar)
    SeekBar cuteSeekBar;

    @BindView(R.id.cute_song_image)
    ImageView cuteSongImage;

    @BindView(R.id.cute_song_name)
    TextView cuteSongName;

    @BindView(R.id.cute_song_artist)
    TextView cuteSongArtist;

    @BindView(R.id.cute_image_button_go)
    ImageView cuteImageButtonGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        settingThingsUp();
    }

    private void settingThingsUp() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        goContent();
    }

    private void goContent() {
        openFragment(this.getClass().getSimpleName(), new MainActivityFragment(), false);

        cutePlayer.setVisibility(View.GONE);
        getAnimations();
    }

    private RotateAnimation rotateAnimation;

    private void getAnimations() {
        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        cuteSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean byUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                countDownTimerCancel(-1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayer.getInstance().seekTo(seekBar.getProgress());
                remainTime = zTotalTime - seekBar.getProgress();
                countDownTimerCancel(remainTime);
            }
        });
    }

    @Subscribe
    public void onFragmentEvent(FragmentChanger changer) {
        openFragment(changer.getSource(), changer.getFragment(), changer.isAddToBackStack());
    }

    private void openFragment(String source, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (source.equals(GenresFragment.class.getSimpleName())) {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                    R.anim.go_right_in, R.anim.go_right_out);
        }

        fragmentTransaction.replace(R.id.layout_main, fragment);
        if (addToBackStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * PLAYER ================================================================================
     */

    private Genre genre;
    private Song song;
    private int indexSong;
    private boolean isPlaying;
    private boolean isWaiting;

    private CountDownTimer countDownTimer;
    private long remainTime;
    private long zTotalTime;

    @Subscribe
    public void onSongEvent(SongChanger event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;
        genre = RealmManager.getInstance().getGenres().get(event.getIndexGenre());
        if (RealmManager.getInstance().getSongs(genre.getNumber()).isEmpty()) return;
        prePlay(event.getIndexSong());
    }

    private void prePlay(int indexSong) {
        this.indexSong = indexSong;
        song = RealmManager.getInstance().getSongs(genre.getNumber()).get(indexSong);
        String search = song.getName() + " - " + song.getArtist();
        System.out.println("searching " + search);

        changeWaiting(true);

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.GET_MP3_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getSongMp3(search).enqueue(new Callback<SongMp3>() {
            @Override
            public void onResponse(Call<SongMp3> call, Response<SongMp3> response) {
                System.out.println("getSongMp3 onResponse");
                changeWaiting(false);

                SongMp3 mp3song = response.body();
                if (mp3song == null)
                    System.out.println("NOT FOUND");
                else MusicPlayer.getInstance().prepare(mp3song.getStream());
            }

            @Override
            public void onFailure(Call<SongMp3> call, Throwable t) {
                System.out.println("getSongMp3 onFailure");
                changeWaiting(false);
            }
        });
    }

    @Subscribe
    public void goPlay(Notifier event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;

        isPlaying = true;
        zTotalTime = MusicPlayer.getInstance().getDuration();
        cuteSeekBar.setMax((int) zTotalTime);
        cuteSeekBar.setProgress(0);
        cuteSongName.setText(song.getName());
        cuteSongArtist.setText(song.getArtist());
        cuteImageButtonGo.setImageResource(R.drawable.ic_pause_white_48px);
        ImageLoader.getInstance().displayImage(song.getImageLink(), cuteSongImage);
        cuteSongImage.startAnimation(rotateAnimation);

        countDownTimerCancel(zTotalTime);

        cutePlayer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cute_button_go)
    public void goCuteButton() {
        MusicPlayer.getInstance().changeState();
        if (isPlaying) {
            isPlaying = false;
            cuteImageButtonGo.setImageResource(R.drawable.ic_play_arrow_white_48px);
            cuteSongImage.clearAnimation();
            countDownTimerCancel(-1);
        } else {
            isPlaying = true;
            cuteImageButtonGo.setImageResource(R.drawable.ic_pause_white_48px);
            cuteSongImage.startAnimation(rotateAnimation);
            countDownTimerCancel(remainTime);
        }
    }

    private void nextSong() {
        if (isWaiting) return;
        prePlay(++indexSong % 50);
    }

    private void countDownTimerCancel(long millisInFuture) {
        if (countDownTimer != null) countDownTimer.cancel();
        if (millisInFuture == -1) return;
        countDownTimer = new CountDownTimer(millisInFuture + 1, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainTime = millisUntilFinished;
                cuteSeekBar.setProgress((int) (zTotalTime - remainTime));
            }

            @Override
            public void onFinish() {
                nextSong();
            }
        }.start();
    }

    private void changeWaiting(boolean waiting) {
        isWaiting = waiting;
        EventBus.getDefault().post(new WaitingChanger(
                SongsFragment.class.getSimpleName(), waiting));
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
