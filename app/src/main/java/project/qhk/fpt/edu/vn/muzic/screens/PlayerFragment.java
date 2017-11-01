package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.qhk.fpt.edu.vn.muzic.Constant;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.managers.MusicPlayer;
import project.qhk.fpt.edu.vn.muzic.models.Genre;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.objects.PlayerNotifier;
import project.qhk.fpt.edu.vn.muzic.objects.WaitingChanger;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    @BindView(R.id.player_seek_bar)
    SeekBar playerSeekBar;

    @BindView(R.id.player_song_image)
    ImageView playerSongImage;

    @BindView(R.id.player_song_name)
    TextView playerSongName;

    @BindView(R.id.player_song_artist)
    TextView playerSongArtist;

    @BindView(R.id.player_image_button_go)
    ImageView playerImageButtonGo;

    @BindView(R.id.player_time_passed)
    TextView playerTimePassed;

    @BindView(R.id.player_time_total)
    TextView playerTimeTotal;

    @BindView(R.id.player_bar_waiting)
    ProgressBar waitingBar;

    MainActivity activity;

    private Song song;
    private boolean isPlaying;
    private boolean isWaiting;

    private CountDownTimer countDownTimer;
    private int remainTime;
    private int zTotalTime;

    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean byUser) {
                playerTimePassed.setText(Constant.toTime(progress));
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

    @Override
    public void onResume() {
        super.onResume();
        getContent();
        new CountDownTimer(500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                activity.getLayoutDaddy().setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.getLayoutDaddy().setVisibility(View.VISIBLE);
    }

    private void getContent() {
        activity = (MainActivity) getActivity();
        song = activity.getSong();
        isPlaying = activity.isPlaying();

        zTotalTime = MusicPlayer.getInstance().getDuration();
        remainTime = zTotalTime - MusicPlayer.getInstance().getProgress();

        playerTimeTotal.setText(Constant.toTime(zTotalTime));
        playerTimePassed.setText(Constant.toTime(zTotalTime - remainTime));

        playerSeekBar.setMax(zTotalTime);
        playerSeekBar.setProgress(zTotalTime - remainTime);

        playerSongName.setText(song.getName());
        playerSongArtist.setText(song.getArtist());
        ImageLoader.getInstance().displayImage(song.getImagePicture(), playerSongImage);
        waitingBar.setVisibility(View.INVISIBLE);

        if (isPlaying) {
            playerImageButtonGo.setImageResource(R.drawable.ic_pause_white_48px);
            countDownTimerCancel(remainTime);
        } else {
            playerImageButtonGo.setImageResource(R.drawable.ic_play_arrow_white_48px);
            countDownTimerCancel(-1);
        }
    }

    private void countDownTimerCancel(long millisInFuture) {
        if (countDownTimer != null) countDownTimer.cancel();
        if (millisInFuture == -1) return;
        countDownTimer = new CountDownTimer(millisInFuture + 1, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainTime = (int) millisUntilFinished;
                playerSeekBar.setProgress(zTotalTime - remainTime);
            }

            @Override
            public void onFinish() {
                activity.goNextSong();
            }
        }.start();
    }

    @Subscribe
    public void onSubscribe(PlayerNotifier event) {
        song = activity.getSong();
        isPlaying = activity.isPlaying();

        zTotalTime = MusicPlayer.getInstance().getDuration();
        remainTime = zTotalTime - MusicPlayer.getInstance().getProgress();

        playerSeekBar.setMax(zTotalTime);
        playerSeekBar.setProgress(zTotalTime - remainTime);

        playerTimeTotal.setText(Constant.toTime(zTotalTime));
        playerTimePassed.setText(Constant.toTime(zTotalTime - remainTime));

        playerSongName.setText(song.getName());
        playerSongArtist.setText(song.getArtist());
        ImageLoader.getInstance().displayImage(song.getImagePicture(), playerSongImage);

        if (isPlaying) {
            playerImageButtonGo.setImageResource(R.drawable.ic_pause_white_48px);
            countDownTimerCancel(remainTime);
        } else {
            playerImageButtonGo.setImageResource(R.drawable.ic_play_arrow_white_48px);
        }
    }

    @OnClick(R.id.player_button_go)
    public void goPlayerButton() {
        activity.goCuteButton();
        if (isPlaying) {
            isPlaying = false;
            playerImageButtonGo.setImageResource(R.drawable.ic_play_arrow_white_48px);
            countDownTimerCancel(-1);
        } else {
            isPlaying = true;
            playerImageButtonGo.setImageResource(R.drawable.ic_pause_white_48px);
            countDownTimerCancel(remainTime);
        }
    }

    @OnClick(R.id.player_button_next)
    public void goPlayerNext() {
        activity.goNextSong();
    }

    @OnClick(R.id.player_button_previous)
    public void goPlayerPrevious() {
        if (MusicPlayer.getInstance().getProgress() < 1000)
            activity.goPreviousSong();
        else {
            MusicPlayer.getInstance().seekTo(0);
            remainTime = zTotalTime - MusicPlayer.getInstance().getProgress();

            playerSeekBar.setProgress(zTotalTime - remainTime);
            playerTimePassed.setText(Constant.toTime(zTotalTime - remainTime));
            if (isPlaying)
                countDownTimerCancel(remainTime);
            else countDownTimerCancel(-1);
        }
    }

    @OnClick(R.id.player_button_back)
    public void onBackPressed() {
        countDownTimerCancel(-1);
        activity.goContinue();
        getActivity().onBackPressed();
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
