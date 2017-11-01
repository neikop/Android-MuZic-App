package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import org.greenrobot.eventbus.EventBus;

import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.objects.Notifier;
import project.qhk.fpt.edu.vn.muzic.objects.WaitingChanger;
import project.qhk.fpt.edu.vn.muzic.screens.SongsFragment;

/**
 * Created by WindzLord on 10/29/2017.
 */

public class MusicPlayer {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private ExoPlayer exoPlayer;
    private Context context;

    private MusicPlayer(Context context) {
        this.context = context;
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Factory.newInstance(1);
            exoPlayer.setPlayWhenReady(true);

            exoPlayer.addListener(new ExoPlayer.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    System.out.println("onPlayerStateChanged: " + playbackState);

                    if (playbackState == ExoPlayer.STATE_PREPARING) {
                        new CountDownTimer(200, 1) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                exoPlayer.seekTo(0);
                                EventBus.getDefault().post(new Notifier(MainActivity.class.getSimpleName()));
                            }
                        }.start();
                    }
                }

                @Override
                public void onPlayWhenReadyCommitted() {
                    System.out.println("onPlayWhenReadyCommitted");
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    System.out.println("onPlayerError: " + error.getMessage());
                }
            });
        }
    }

    public long getDuration() {
        return exoPlayer.getDuration();
    }

    public void prepare(String stream) {
        Uri radioUri = Uri.parse(stream);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(context, "MusicPlayer");
        DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        exoPlayer.prepare(audioRenderer);
    }

    public void seekTo(int progress) {
        exoPlayer.seekTo(progress);
    }

    public void resume() {
        exoPlayer.setPlayWhenReady(true);
    }

    public void changeState() {
        exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
    }

    private static MusicPlayer instance;

    public static MusicPlayer getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new MusicPlayer(context);
    }

    private void changeWaiting(boolean waiting) {
        EventBus.getDefault().post(new WaitingChanger(
                SongsFragment.class.getSimpleName(), waiting));
    }
}
