package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;
import android.net.Uri;

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
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.notifiers.SimpleNotifier;

/**
 * Created by WindzLord on 10/29/2017.
 */

public class MusicPlayer {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static MusicPlayer instance;
    private ExoPlayer exoPlayer;
    private boolean readyPost;
    private Song song;

    private MusicPlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Factory.newInstance(1);

            exoPlayer.addListener(new ExoPlayer.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    System.out.println("onPlayerStateChanged: " + playbackState);

                    if (playbackState == ExoPlayer.STATE_PREPARING) readyPost = true;
                    if (playbackState == ExoPlayer.STATE_READY) {
                        if (readyPost) {
                            readyPost = false;
                            EventBus.getDefault().post(new SimpleNotifier(MainActivity.class.getSimpleName()));
                            exoPlayer.setPlayWhenReady(true);
                        }
                    }
                }

                @Override
                public void onPlayWhenReadyCommitted() {
                    System.out.println("onPlayWhenReadyCommitted: " + exoPlayer.getPlaybackState());
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    System.out.println("onPlayerError: " + error.getMessage());
                }
            });
        }
    }

    public static MusicPlayer getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new MusicPlayer();
    }

    public void prepare(Context context, String stream, Song song) {
        Uri radioUri = Uri.parse(stream);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(context, "MusicPlayer");
        DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        exoPlayer.seekTo(0);
        exoPlayer.prepare(audioRenderer);

        this.song = song;
    }

    public int getProgress() {
        return (int) exoPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return (int) exoPlayer.getDuration();
    }

    public void seekTo(int progress) {
        exoPlayer.seekTo(progress);
    }

    public void changeState() {
        exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
    }

    public Song getSong() {
        return song;
    }

}
