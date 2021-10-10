package com.manuelsoft.mypomodoroapp.chronometer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_ONE_TICK_TEST;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_TICK;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.TIME;

class ChronometerManager {

    private final Context context;
    private final ChronometerTimer chronometerTimer;
    private Handler chronometerHandler;
    private final SoundHelper soundHelper;
    private boolean isRunning = false;

    ChronometerManager(@NonNull Context context) {
        assert context != null : "Context is null";
        this.context = context;
        chronometerTimer = new ChronometerTimer();
        soundHelper = new SoundHelper(context);
    }

    public void sendMessage(String action, String name, String message) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (name != null) {
            intent.putExtra(name, message);
        }
        sendLocalBroadcast(intent);
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void setChronometer(int pomodoroMinutes) {
        ChronometerTask task = (minutes, seconds, counter) -> {
            String time = chronometerTimer.print(minutes, seconds);
            sendMessage(ACTION_TICK, TIME, time);
        };

        ChronometerTask end = (minutes, seconds, counter) -> {
            isRunning = false;
            sendMessage(ACTION_FINISH, null, null);
            chronometerHandler.getLooper().quit();
            // chronometerHandler.removeCallbacksAndMessages(null);
            soundHelper.stop();
        };

        chronometerTimer.set(pomodoroMinutes, task, end);
    }

    public void startChronometer() {
//        handler.post(() -> {
//            Log.d(TAG, Looper.myLooper().getThread().getName());
//            chronometerTask.execute();
//        });
        isRunning = true;
        Thread thread = new Thread(() -> {
            Looper.prepare();
            chronometerHandler = new Handler(Looper.myLooper());
            chronometerTimer.execute();
            Looper.loop();
        });
        thread.start();
        soundHelper.play();
    }

    public void stopChronometer() {
        isRunning = false;
        chronometerHandler.post(chronometerTimer::cancel);
        chronometerHandler.getLooper().quit();
        // chronometerHandler.removeCallbacksAndMessages(null);
        soundHelper.stop();
    }

    public boolean chronometerIsActive() {
        return isRunning;
    }

    public void onDestroy() {
        if (isRunning) {
            isRunning = false;
            chronometerHandler.getLooper().quit();
        }
        soundHelper.release();
    }

    @VisibleForTesting
    public void sendOneTick() {
        new Thread() {
            @Override
            public void run() {
                sendMessage(ACTION_ONE_TICK_TEST, null, null);
            }
        }.start();
    }

    @VisibleForTesting
    public void start5secCount() {

    }
}
