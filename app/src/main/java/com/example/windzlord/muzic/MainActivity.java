package com.example.windzlord.muzic;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.windzlord.muzic.managers.MusicPlayer;
import com.example.windzlord.muzic.managers.RealmManager;
import com.example.windzlord.muzic.models.Genre;
import com.example.windzlord.muzic.models.Playlist;
import com.example.windzlord.muzic.models.Song;
import com.example.windzlord.muzic.models.api_models.SongMp3;
import com.example.windzlord.muzic.notifiers.FragmentChanger;
import com.example.windzlord.muzic.notifiers.SimpleNotifier;
import com.example.windzlord.muzic.notifiers.SongChanger;
import com.example.windzlord.muzic.notifiers.WaitingChanger;
import com.example.windzlord.muzic.screens.FavourFragment;
import com.example.windzlord.muzic.screens.FavourSongFragment;
import com.example.windzlord.muzic.screens.GenresFragment;
import com.example.windzlord.muzic.screens.LoginFragment;
import com.example.windzlord.muzic.screens.PlayerFragment;
import com.example.windzlord.muzic.screens.SearchFragment;
import com.example.windzlord.muzic.screens.SettingFragment;
import com.example.windzlord.muzic.screens.SongFragment;
import com.example.windzlord.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

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
    Song playingSong;

    /**
     * PLAYER ================================================================================
     */

    private Genre genre;
    private Playlist playlist;

    private Song song;
    private int indexSong;

    private boolean isPlaying;
    private boolean isWaiting;
    private boolean isSyncing = false;

    private boolean isPlayingGenre;

    private CountDownTimer countDownTimer;
    private int remainTime;
    private int zTotalTime;

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
        openFragment(this.getClass().getSimpleName(), new SettingFragment(), false);

        cutePlayer.setVisibility(View.GONE);
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
                if (isPlaying) countDownTimerCancel(remainTime);
            }
        });
    }

    @Subscribe
    public void onFragmentEvent(FragmentChanger changer) {
        if (changer.getSource().equals(SettingFragment.class.getSimpleName())) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.go_down_in, R.anim.do_nothing, R.anim.do_nothing, R.anim.go_down_out)
                    .replace(R.id.layout_mommy, new LoginFragment())
                    .addToBackStack(null).commit();

            new CountDownTimer(500, 100) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    EventBus.getDefault().post(new SimpleNotifier(SettingFragment.class.getSimpleName()));
                }
            }.start();
            return;
        }

        openFragment(changer.getSource(), changer.getFragment(), changer.isAddToBackStack());
    }

    private void openFragment(String source, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (source.equals(GenresFragment.class.getSimpleName())
                || source.equals(FavourFragment.class.getSimpleName())) {
            fragmentTransaction.setCustomAnimations(R.anim.go_fade_in, R.anim.go_fade_out,
                    R.anim.go_right_in, R.anim.go_right_out);
        }

        fragmentTransaction.replace(R.id.layout_main, fragment);

        if (addToBackStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void onSongEvent(SongChanger event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;

        if (event.getGenre() != null) {
            genre = event.getGenre();
            if (RealmManager.getInstance().getSongs(genre.getGenreID()).isEmpty()) return;
            isPlayingGenre = true;
            prePlay(event.getIndexSong(), true);
        } else {
            playlist = event.getPlaylist();
            if (RealmManager.getInstance().getSongsPlaylist(playlist.getPlaylistID()).isEmpty())
                return;
            isPlayingGenre = false;
            prePlay(event.getIndexSong(), false);
        }
    }

    private void prePlay(int indexSong, boolean isPlayGenre) {
        this.indexSong = indexSong;
        if (isPlayGenre)
            song = RealmManager.getInstance().getSongs(genre.getGenreID()).get(indexSong);
        else
            song = RealmManager.getInstance().getSongsPlaylist(playlist.getPlaylistID()).get(indexSong);

        String search = song.getName() + " - " + song.getArtist();
        System.out.println("searching " + search);

        changeWaiting(true);

        if (song.getStream() != null) {
            MusicPlayer.getInstance().prepare(getApplicationContext(), song.getStream(), song);
            return;
        }
        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.GET_MP3_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getSongMp3(search).enqueue(new Callback<SongMp3>() {
            @Override
            public void onResponse(Call<SongMp3> call, Response<SongMp3> response) {
                System.out.println("getSongMp3 onResponse");

                SongMp3 mp3song = response.body();
                if (mp3song == null) {
                    Toast.makeText(getApplicationContext(), "NOT FOUND", Toast.LENGTH_SHORT).show();
                    changeWaiting(false);
                } else {
                    RealmManager.getInstance().editSongPicture(song, mp3song.getPicture());
                    RealmManager.getInstance().editSongStream(song, mp3song.getStream());
                    MusicPlayer.getInstance().prepare(getApplicationContext(), mp3song.getStream(), song);
                }
            }

            @Override
            public void onFailure(Call<SongMp3> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                changeWaiting(false);
            }
        });
    }

    @Subscribe
    public void goPlay(SimpleNotifier event) {
        if (!this.getClass().getSimpleName().equals(event.getTarget())) return;
        changeWaiting(false);

        isPlaying = true;
        playingSong = song;
        EventBus.getDefault().post(new SimpleNotifier(PlayerFragment.class.getSimpleName()));

        zTotalTime = MusicPlayer.getInstance().getDuration();
        remainTime = zTotalTime - MusicPlayer.getInstance().getProgress();

        cuteSeekBar.setMax(zTotalTime);
        cuteSeekBar.setProgress(zTotalTime - remainTime);

        cuteSongName.setText(song.getName());
        cuteSongArtist.setText(song.getArtist());
        if (song.getImageLink() != null)
            ImageLoader.getInstance().displayImage(song.getImageLink(), cuteSongImage);
        else cuteSongImage.setImageResource(R.drawable.image_song_demo);
        cuteSongImage.startAnimation(Logistic.getRotateAnimation());

        cuteImageButtonGo.setImageResource(R.drawable.ic_pause_white_48px);
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
            cuteSongImage.startAnimation(Logistic.getRotateAnimation());
            countDownTimerCancel(remainTime);
        }
    }

    public void goNextSong() {
        if (isWaiting) return;
        if (isPlayingGenre) {
            if (RealmManager.getInstance().getSongs(genre.getGenreID()).isEmpty()) return;

            int number = RealmManager.getInstance().getSongs(genre.getGenreID()).size();
            prePlay(++indexSong % number, true);
        } else {
            if (RealmManager.getInstance().getSongsPlaylist(playlist.getPlaylistID()).isEmpty())
                return;

            int number = RealmManager.getInstance().getSongsPlaylist(playlist.getPlaylistID()).size();
            prePlay(++indexSong % number, true);
        }
    }

    public void goPreviousSong() {
        if (isWaiting) return;
        if (isPlayingGenre) {
            if (RealmManager.getInstance().getSongs(genre.getGenreID()).isEmpty()) return;

            int number = RealmManager.getInstance().getSongs(genre.getGenreID()).size();
            prePlay((indexSong + number - 1) % number, true);
        } else {
            if (RealmManager.getInstance().getSongsPlaylist(playlist.getPlaylistID()).isEmpty())
                return;

            int number = RealmManager.getInstance().getSongsPlaylist(playlist.getPlaylistID()).size();
            prePlay((indexSong + number - 1) % number, true);
        }

    }

    private void countDownTimerCancel(long millisInFuture) {
        if (countDownTimer != null) countDownTimer.cancel();

        if (isSyncing) return;
        if (millisInFuture == -1) return;

        countDownTimer = new CountDownTimer(millisInFuture + 1, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainTime = (int) millisUntilFinished;
                cuteSeekBar.setProgress(zTotalTime - remainTime);
            }

            @Override
            public void onFinish() {
                goNextSong();
            }
        }.start();
    }

    @OnClick(R.id.cute_player)
    public void goPlayer() {
        if (isSyncing) return;
        if (isWaiting) return;

        isSyncing = true;
        countDownTimerCancel(-1);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.go_up_in, R.anim.do_nothing, R.anim.do_nothing, R.anim.go_down_out)
                .replace(R.id.layout_mommy, new PlayerFragment())
                .addToBackStack(null).commit();
    }

    private void changeWaiting(boolean waiting) {
        isWaiting = waiting;
        EventBus.getDefault().post(new WaitingChanger(FavourSongFragment.class.getSimpleName(), waiting));
        EventBus.getDefault().post(new WaitingChanger(SongFragment.class.getSimpleName(), waiting));
        EventBus.getDefault().post(new WaitingChanger(SearchFragment.class.getSimpleName(), waiting));
        EventBus.getDefault().post(new WaitingChanger(PlayerFragment.class.getSimpleName(), waiting));
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * PLAYER ================================================================================
     */

    public void setLayoutDaddy(int visibility) {
        findViewById(R.id.layout_daddy).setVisibility(visibility);
    }

    public Song getSong() {
        return song;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void goContinue() {
        isSyncing = false;

        zTotalTime = MusicPlayer.getInstance().getDuration();
        remainTime = zTotalTime - MusicPlayer.getInstance().getProgress();

        cuteSeekBar.setMax(zTotalTime);
        cuteSeekBar.setProgress(zTotalTime - remainTime);

        cuteSongName.setText(playingSong.getName());
        cuteSongArtist.setText(playingSong.getArtist());
        if (playingSong.getImageLink() != null)
            ImageLoader.getInstance().displayImage(playingSong.getImageLink(), cuteSongImage);
        else cuteSongImage.setImageResource(R.drawable.image_song_demo);

        if (isPlaying) {
            cuteImageButtonGo.setImageResource(R.drawable.ic_pause_white_48px);
            cuteSongImage.startAnimation(Logistic.getRotateAnimation());
            countDownTimerCancel(remainTime);
        } else {
            cuteImageButtonGo.setImageResource(R.drawable.ic_play_arrow_white_48px);
            cuteSongImage.clearAnimation();
        }
        setLayoutDaddy(View.VISIBLE);
    }

}
