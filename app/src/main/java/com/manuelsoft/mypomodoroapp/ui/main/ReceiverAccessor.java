package com.manuelsoft.mypomodoroapp.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.manuelsoft.mypomodoroapp.BuildConfig;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_5_SECONDS_TEST;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_TICK;

class ReceiverAccessor {
    private BroadcastReceiver receiver;
    private final Context context;
    private boolean receiverRegistered = false;
    private IntentFilter receiverIntentFilter;
    public interface OnReceive {
        void run(Context context, Intent intent);
    }
    private final OnReceive onReceive;

    public ReceiverAccessor(@NonNull Context context, @NonNull OnReceive onReceive) {
        assert context != null : "Context is null";
        assert onReceive != null : "Receiver is null";

        this.context = context;
        this.onReceive = onReceive;
        setupReceiverIntentFilter();
        createReceiver();
    }

    private void setupReceiverIntentFilter() {
        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(ACTION_TICK);
        receiverIntentFilter.addAction(ACTION_FINISH);
        if (BuildConfig.DEBUG) {
            receiverIntentFilter.addAction(ACTION_5_SECONDS_TEST);
        }
    }

    private void createReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceive.run(context, intent);
            }
        };
    }


    private void registerReceiver() {
        if (!receiverRegistered) {
            receiverRegistered = true;
            LocalBroadcastManager.getInstance(context.getApplicationContext())
                    .registerReceiver(receiver, receiverIntentFilter);
        }
    }

    private void unregisterReceiver() {
        if (receiverRegistered) {
            receiverRegistered = false;
            LocalBroadcastManager.getInstance(context.getApplicationContext())
                    .unregisterReceiver(receiver);
        }
    }

    public void connect() {
        Log.d(MainActivity.TAG, "connect");
        if (receiver == null) {
            Log.d(MainActivity.TAG, "Receiver is null");
        }
        registerReceiver();
    }

    public void disconnect() {
        Log.d(MainActivity.TAG, "disconnect");

        unregisterReceiver();
    }

}
