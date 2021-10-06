package com.manuelsoft.mypomodoroapp.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.manuelsoft.mypomodoroapp.BuildConfig;

import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_5_SECONDS_TEST;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_TICK;

class MainActivityLifecycleObserver implements LifecycleObserver {
    private final BroadcastReceiver receiver;
    private final Context context;
    private boolean receiverRegistered = false;
    private IntentFilter receiverIntentFilter;

    public MainActivityLifecycleObserver(@NonNull Context context, @NonNull BroadcastReceiver receiver) {
        assert context != null : "Context is null";
        assert receiver != null : "Receiver is null";

        this.context = context;
        this.receiver = receiver;
        setupReceiverIntentFilter();
    }

    private void setupReceiverIntentFilter() {
        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(ACTION_TICK);
        receiverIntentFilter.addAction(ACTION_FINISH);
        if (BuildConfig.DEBUG) {
            receiverIntentFilter.addAction(ACTION_5_SECONDS_TEST);
        }
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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void connect() {
        Log.d(MainActivity.TAG, "connect");
        if (receiver == null) {
            Log.d(MainActivity.TAG, "Receiver is null");
        }
        registerReceiver();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void disconnect() {
        Log.d(MainActivity.TAG, "disconnect");

        unregisterReceiver();
    }

}
