package com.manuelsoft.mypomodoroapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MyChronometerService extends Service {

    private final IBinder binder = new MyChronometerBinder();
    private MyChronometerTask myChronometerTask;
    private Handler chronometerHandler;
    private boolean isRunning = false;
    private final String TAG = MyChronometerService.class.getName();
    public static final String TIME = "time";
    public static final String VENDOR = "com.manuelsoft.mypomodoroapp.";
    public static final String ACTION_TICK = VENDOR + "tick";
    public static final String ACTION_FINISH = VENDOR + ".finish";
    public static final String ACTION_TEST = VENDOR + "test";

    @Override
    public void onCreate() {
        super.onCreate();
        myChronometerTask = new MyChronometerTask();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startForeground(NOTIFICATION_ID, notification);
        return START_NOT_STICKY;
    }

    public void sendMessage(String action, String name, String message) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (name != null) {
            intent.putExtra(name, message);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public class MyChronometerBinder extends Binder {

        public MyChronometerService getService() {
            return MyChronometerService.this;
        }
    }

    public boolean isActive() {
        return true;
    }

    public void setChronometer(int pomodoroMinutes) {

        MyTask myTask = (minutes, seconds, counter) -> {
            String time = myChronometerTask.print(minutes, seconds);
            sendMessage(ACTION_TICK, TIME, time);
        };

        MyTask end = (minutes, seconds, counter) -> {
            isRunning = false;
            sendMessage(ACTION_FINISH, null, null);
            chronometerHandler.getLooper().quit();
            // chronometerHandler.removeCallbacksAndMessages(null);
        };

        myChronometerTask.set(pomodoroMinutes, myTask, end);
    }

    public void startChronometer() {
//        handler.post(() -> {
//            Log.d(TAG, Looper.myLooper().getThread().getName());
//            myChronometerTask.execute();
//        });
        isRunning = true;
        Thread thread = new Thread(() -> {
            Looper.prepare();
            chronometerHandler = new Handler(Looper.myLooper());
            myChronometerTask.execute();
            Looper.loop();
        });
        thread.start();
    }

    public void stopChronometer() {
        isRunning = false;
        chronometerHandler.post(myChronometerTask::cancel);
        chronometerHandler.getLooper().quit();
        // chronometerHandler.removeCallbacksAndMessages(null);
    }


    public boolean chronometerIsActive() {
        return isRunning;
    }

    @VisibleForTesting
    public void sendOneTick() {
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "Sending ACTION_TEST");
                sendMessage(ACTION_TEST, null, null);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        if (isRunning) {
            isRunning = false;
            chronometerHandler.getLooper().quit();
        }
        super.onDestroy();
    }
}
