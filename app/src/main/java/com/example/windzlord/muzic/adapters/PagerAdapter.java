package com.example.windzlord.muzic.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.windzlord.muzic.screens.FavourFragment;
import com.example.windzlord.muzic.screens.GenresFragment;
import com.example.windzlord.muzic.screens.SearchFragment;

/**
 * Created by WindzLord on 11/28/2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numberOfTab;

    public PagerAdapter(FragmentManager manager, int numberOfTab) {
        super(manager);
        this.numberOfTab = numberOfTab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GenresFragment();
            case 1:
                return new FavourFragment();
            case 2:
                return new SearchFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTab;
    }
}