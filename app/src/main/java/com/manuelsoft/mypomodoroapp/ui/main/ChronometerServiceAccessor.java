package com.manuelsoft.mypomodoroapp.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.manuelsoft.mypomodoroapp.chronometer.ChronometerService;
import com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ChronometerBinder;
import com.manuelsoft.mypomodoroapp.common.Utilities;

class ChronometerServiceAccessor {
    private final Context context;
    private boolean bound = false;
    private ServiceConnection connection;
    private ChronometerService service;
    public static final String TAG = ChronometerServiceAccessor.class.getName();


    public ChronometerServiceAccessor(Context context) {
        this.context = context;
        startService();
        setupServiceConnection();
        bindService();
    }

    public void startService() {
        Intent intent = new Intent(context.getApplicationContext(), ChronometerService.class);
        context.startService(intent);
    }

    @VisibleForTesting
    public void stopForegroundService() {
        Intent intent = new Intent(context.getApplicationContext(), ChronometerService.class);
        context.stopService(intent);
    }

    public void setupServiceConnection() {
        connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                bound = true;
                ChronometerBinder binder = (ChronometerBinder) service;
                ChronometerServiceAccessor.this.service = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };
    }

    public void bindService() {
        Intent intent = new Intent(context.getApplicationContext(), ChronometerService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        if (bound) {
            context.unbindService(connection);
            bound = false;
        }
    }

    public void startChronometer(int minutes) {
        if (bound) {
            service.setChronometer(minutes);
            service.startChronometer();
        }
        Log.d(TAG, "Sending message");
        Log.d(TAG, "service running: " + Utilities.isServiceRunning(context, ChronometerService.class));
    }

    public void stopChronometer() {
        if (bound) {
            service.stopChronometer();
        }
    }

    @VisibleForTesting
    public void sendOneTick() {
        if (bound) {
            service.sendOneTick();
        }
    }

    @VisibleForTesting
    public void start5secCount() {
        if (bound) {
            service.start5secCount();
        }
    }
}
