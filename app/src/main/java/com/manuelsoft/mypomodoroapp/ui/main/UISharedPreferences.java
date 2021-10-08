package com.manuelsoft.mypomodoroapp.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.manuelsoft.mypomodoroapp.ui.main.MainActivity.CHRONOMETER_IS_RUNNING;
import static com.manuelsoft.mypomodoroapp.ui.main.MainActivity.TIME_SELECTED;
import static com.manuelsoft.mypomodoroapp.ui.main.MainActivity.UI_SHARED_PREFERENCES;
import static com.manuelsoft.mypomodoroapp.ui.main.MainActivityViewModel.TWENTY;

class UISharedPreferences {

    public static final String TAG = UISharedPreferences.class.getName();
    private final Context context;

    public UISharedPreferences(Context context) {
        this.context = context;
    }

    public SharedPreferences getUISharedPreferences() {
        return context.getSharedPreferences(UI_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveChronometerState(boolean chronometerIsRunning, int time) {
        getUISharedPreferences().edit()
                .putBoolean(CHRONOMETER_IS_RUNNING, chronometerIsRunning)
                .putInt(TIME_SELECTED, time)
                .apply();
    }

    public boolean loadChronometerIsRunning() {
        boolean result = getUISharedPreferences().getBoolean(CHRONOMETER_IS_RUNNING, false);
        Log.d(TAG, "loadChronometerIsRunning(): " + result);
        return result;
    }

    public int loadTimeSelected() {
        return getUISharedPreferences().getInt(TIME_SELECTED, TWENTY);
    }

}
