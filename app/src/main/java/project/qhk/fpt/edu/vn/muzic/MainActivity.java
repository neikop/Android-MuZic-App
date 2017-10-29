package project.qhk.fpt.edu.vn.muzic;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.objects.FragmentChanger;
import project.qhk.fpt.edu.vn.muzic.screens.MainActivityFragment;

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
    }

    @Subscribe
    public void onFragmentEvent(FragmentChanger changer) {
        openFragment(changer.getSource(), changer.getFragment(), changer.isAddToBackStack());
    }

    private void openFragment(String source, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.layout_main, fragment);
        if (addToBackStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
