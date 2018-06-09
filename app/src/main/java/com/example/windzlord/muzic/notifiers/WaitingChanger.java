package com.example.windzlord.muzic.notifiers;

/**
 * Created by WindzLord on 10/29/2017.
 */

public class WaitingChanger {

    private String target;
    private boolean waiting;

    public WaitingChanger(String target, boolean waiting) {
        this.target = target;
        this.waiting = waiting;
    }

    public String getTarget() {
        return target;
    }

    public boolean isWaiting() {
        return waiting;
    }
}
