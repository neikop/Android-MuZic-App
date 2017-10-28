package project.qhk.fpt.edu.vn.muzic;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.objects.FragmentChanger;
import project.qhk.fpt.edu.vn.muzic.screens.MainActivityFragment;

public class MainActivity extends AppCompatActivity {

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
}
