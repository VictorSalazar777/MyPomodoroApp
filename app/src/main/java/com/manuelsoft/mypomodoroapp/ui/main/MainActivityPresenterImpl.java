package com.manuelsoft.mypomodoroapp.ui.main;

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private Boolean activeState = false;
    private int minutes = TWENTY;


    @Override
    public void setStateActive() {
        activeState = true;
    }

    @Override
    public void setStateInactive() {
        activeState = false;
    }

    @Override
    public boolean isActive() {
        return activeState;
    }

    @Override
    public void setFifteenMinutes() {
        minutes = FIFTEEN;
    }

    @Override
    public void setTwentyMinutes() {
        minutes = TWENTY;
    }

    @Override
    public int getHowManyMinutes() {
        return minutes;
    }


}
