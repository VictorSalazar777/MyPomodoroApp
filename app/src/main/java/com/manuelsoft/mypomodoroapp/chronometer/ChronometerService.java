package com.manuelsoft.mypomodoroapp.chronometer;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;


public class ChronometerService extends Service {

    private final String TAG = ChronometerService.class.getName();
    private final IBinder binder = new ChronometerBinder();
    public static final String TIME = "time";
    public static final String VENDOR = "com.manuelsoft.mypomodoroapp.";
    public static final String ACTION_TICK = VENDOR + "tick";
    public static final String ACTION_FINISH = VENDOR + "finish";
    public static final String ACTION_ONE_TICK_TEST = VENDOR + "one_tick_test";
    public static final String ACTION_5_SEC_TEST_FINISH = VENDOR + "5_sec_test";
    public static final int NOTIFICATION_SERVICE_ID = 1;
    public static final String POMODORO_CHANNEL_ID = "channel_1";
    private Notification notification;
    private ChronometerManager chronometerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();
        notification = notificationHelper.createNotification();
        chronometerManager = new ChronometerManager(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class ChronometerBinder extends Binder {
        public ChronometerService getService() {
            return ChronometerService.this;
        }
    }

    public boolean isActive() {
        return true;
    }

    public void setChronometer(int pomodoroMinutes) {
        chronometerManager.setChronometer(pomodoroMinutes);
    }

    public void startChronometer() {
        startForeground(NOTIFICATION_SERVICE_ID, notification);
        chronometerManager.startChronometer();
    }

    public void stopChronometer() {
        chronometerManager.stopChronometer();
        stopForeground(true);
    }

    public boolean chronometerIsActive() {
        return chronometerManager.chronometerIsActive();
    }

    @VisibleForTesting
    public void sendOneTick() {
        chronometerManager.sendOneTick();
    }

    @VisibleForTesting
    public void start5secCount() {
        chronometerManager.start5secCount();
    }

    @Override
    public void onDestroy() {
        chronometerManager.onDestroy();
        super.onDestroy();
    }

}
