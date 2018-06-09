package com.example.windzlord.muzic.notifiers;

import android.support.v4.app.Fragment;

/**
 * Created by WindzLord on 11/16/2016.
 */

public class FragmentChanger {

    private String source;
    private Fragment fragment;
    private boolean addToBackStack;

    public FragmentChanger(String source, Fragment fragment, boolean addToBackStack) {
        this.source = source;
        this.fragment = fragment;
        this.addToBackStack = addToBackStack;
    }

    public String getSource() {
        return source;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public boolean isAddToBackStack() {
        return addToBackStack;
    }
}
