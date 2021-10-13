package com.manuelsoft.mypomodoroapp.chronometer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.ui.main.MainActivity;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.POMODORO_CHANNEL_ID;

class NotificationHelper {

    private final Context context;

    NotificationHelper(Context context) {
        this.context = context;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.txt_channel_name);
            String description = context.getString(R.string.txt_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(POMODORO_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(context.getApplicationContext(), POMODORO_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(context.getResources().getColor(android.R.color.holo_green_light))
                .setContentTitle("Pomodoro is running!")
                .setContentText("text")
                .setSubText("Pomodoro is running!")
                //.setTicker() //TODO: Implement this for accessibility
                .setContentIntent(getPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                FLAG_UPDATE_CURRENT
        );
    }

    public Notification createNotification() {
        return getNotificationBuilder().build();
    }

}
