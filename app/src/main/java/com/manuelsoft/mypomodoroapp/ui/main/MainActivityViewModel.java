package com.manuelsoft.mypomodoroapp.ui.main;

import android.util.Log;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    public static final String TAG = MainActivityViewModel.class.getName();
    public static final int FIFTEEN = 15;
    public static final int TWENTY = 20;
    private int minutes = TWENTY;
    private boolean chronometerIsRunning = false;

    public void runChronometer(boolean chronometerIsRunning) {
        this.chronometerIsRunning = chronometerIsRunning;
    }

    public boolean isChronometerRunning() {
        return chronometerIsRunning;
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

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "On cleared");
    }
}
