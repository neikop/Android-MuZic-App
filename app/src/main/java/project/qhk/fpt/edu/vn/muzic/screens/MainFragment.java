package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Playlist;
import project.qhk.fpt.edu.vn.muzic.notifiers.FragmentChanger;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar myToolbar;

    @BindView(R.id.tab_layout)
    TabLayout myTabLayout;

    @BindView(R.id.pager)
    ViewPager myViewPager;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);

        goTabLayout();
    }

    private void goTabLayout() {
        myToolbar.setTitle(Logistic.TITLE);
        myToolbar.inflateMenu(R.menu.menu_main);

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                System.out.println("onMenuItemClick");
                EventBus.getDefault().post(new FragmentChanger(
                        MainFragment.class.getSimpleName(), new LoginFragment(), true));

                RealmManager.getInstance().clearPlaylist();
                RealmManager.getInstance().addPlaylist(Playlist.create("One"));
                RealmManager.getInstance().addPlaylist(Playlist.create("Two"));
                RealmManager.getInstance().addPlaylist(Playlist.create("Three"));
                return true;
            }
        });

        myTabLayout.addTab(myTabLayout.newTab().setText(Logistic.GENRES));
        myTabLayout.addTab(myTabLayout.newTab().setText(Logistic.PLAYLIST));
        myTabLayout.addTab(myTabLayout.newTab().setText(Logistic.OFFLINE));

        myTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        myViewPager.setAdapter(new project.qhk.fpt.edu.vn.muzic.adapters.PagerAdapter(
                getChildFragmentManager(), myTabLayout.getTabCount()) {
        });

        myViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(myTabLayout));
        myTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

}
