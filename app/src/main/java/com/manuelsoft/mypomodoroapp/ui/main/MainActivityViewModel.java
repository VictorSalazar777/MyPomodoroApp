package com.manuelsoft.mypomodoroapp.ui.main;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    public static final int FIFTEEN = 15;
    public static final int TWENTY = 20;
    private Boolean activeState = false;
    private int minutes = TWENTY;

    public void setStateActive() {
        activeState = true;
    }

    public void setStateInactive() {
        activeState = false;
    }

    public boolean isActive() {
        return activeState;
    }

    public void setFifteenMinutes() {
        minutes = FIFTEEN;
    }

    public void setTwentyMinutes() {
        minutes = TWENTY;
    }

    public int getHowManyMinutes() {
        return minutes;
    }

}
