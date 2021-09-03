package com.manuelsoft.mypomodoroapp.ui.main;

public interface MainActivityPresenter {

    int FIFTEEN = 15;
    int TWENTY = 20;

    void setStateActive();
    void setStateInactive();
    boolean isActive();

    void setFifteenMinutes();
    void setTwentyMinutes();
    int getHowManyMinutes();

}