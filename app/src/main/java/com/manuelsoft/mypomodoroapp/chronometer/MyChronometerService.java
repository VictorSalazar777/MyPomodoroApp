package com.manuelsoft.mypomodoroapp.chronometer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.audio.AudioPlayer;
import com.manuelsoft.mypomodoroapp.audio.VolumeContentObserver;
import com.manuelsoft.mypomodoroapp.ui.main.MainActivity;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


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
    public static final int NOTIFICATION_SERVICE_ID = 1;
    public static final String POMODORO_CHANNEL_ID = "channel_1";
    private NotificationCompat.Builder notificationBuilder;
    private AudioPlayer audioPlayer;
    private VolumeContentObserver volumeContentObserver;
    private NotificationManager notificationManager;
    private Handler mainThreadHandler;
    private String lastTime;

    @Override
    public void onCreate() {
        super.onCreate();
        setupAudio();
        createNotificationChannel();
        notificationBuilder = createNotificationBuilder();
        myChronometerTask = new MyChronometerTask();
        notificationManager = getNotificationManager();
        mainThreadHandler = new Handler(getMainLooper());
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

    private void setupAudio() {
        audioPlayer = new AudioPlayer();
        audioPlayer.init();
        audioPlayer.loadSound(this, R.raw.clock);
        volumeContentObserver = new VolumeContentObserver(this, null,
                volume -> audioPlayer.setVolume(volume));
    }

    private void registerVolumeContentObserver() {
        getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        android.provider.Settings.System.CONTENT_URI,
                        true,
                        volumeContentObserver);
    }

    private void unregisterVolumeContentObserver() {
        getApplicationContext()
                .getContentResolver()
                .unregisterContentObserver(volumeContentObserver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(POMODORO_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            getNotificationManager().createNotificationChannel(channel);
        }
    }

    private NotificationManager getNotificationManager() {
        return ContextCompat.getSystemService(this, NotificationManager.class);
    }

    private NotificationCompat.Builder createNotificationBuilder() {
        return new NotificationCompat.Builder(this, POMODORO_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Pomodoro is running!")
                .setContentText("text")
                .setSubText("Pomodoro is running!")
                //.setTicker() //TODO: Implement this for accessibility
                .setContentIntent(getPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                this,
                0,
                intent,
                FLAG_UPDATE_CURRENT
        );
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
            lastTime = time;
            mainThreadHandler.post(() -> {
                notificationBuilder.setContentText(time);
                notificationManager.notify(NOTIFICATION_SERVICE_ID, notificationBuilder.build());
            });
        };

        MyTask end = (minutes, seconds, counter) -> {
            mainThreadHandler.post(() -> {
                stopForeground(true);
                unregisterVolumeContentObserver();
            });

            isRunning = false;
            sendMessage(ACTION_FINISH, null, null);
            chronometerHandler.getLooper().quit();
            // chronometerHandler.removeCallbacksAndMessages(null);
        };

        myChronometerTask.set(pomodoroMinutes, myTask, end);
    }

    public String getLastTime() {
        return lastTime;
    }

    public void startChronometer() {
        startForeground(NOTIFICATION_SERVICE_ID, notificationBuilder.build());
        registerVolumeContentObserver();
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
        audioPlayer.play(volumeContentObserver.getCurrentVolume());
    }

    public void stopChronometer() {
        isRunning = false;
        chronometerHandler.post(myChronometerTask::cancel);
        chronometerHandler.getLooper().quit();
        // chronometerHandler.removeCallbacksAndMessages(null);
        audioPlayer.stop();
        unregisterVolumeContentObserver();
        stopForeground(true);
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
        audioPlayer.release();
        getApplicationContext().getContentResolver().unregisterContentObserver(volumeContentObserver);
        super.onDestroy();
    }


}
