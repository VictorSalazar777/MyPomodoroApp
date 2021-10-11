package com.manuelsoft.mypomodoroapp.chronometer;

import android.os.Handler;
import android.util.Log;

class RepetitiveTask {
    private final Handler handler;
    private final Runnable repetitiveRunnable;
    private volatile boolean isRunning;
    private final long millisecondsDelay;

    public static final String TAG = RepetitiveTask.class.getName();

    public RepetitiveTask(final Runnable runnable, final long millisecondsDelay) {
        isRunning = false;
        this.millisecondsDelay = millisecondsDelay;
        repetitiveRunnable = createRunnable(runnable, millisecondsDelay);
        handler = new Handler();
    }

    public void start(final boolean runImmediately) {
        if (!isRunning) {
            isRunning = true;
            if (runImmediately) {
                if (handler.getLooper().getThread().getId() == Thread.currentThread().getId()) {
                    repetitiveRunnable.run();
                } else {
                    handler.post(repetitiveRunnable);
                }
            } else {
                handler.postDelayed(repetitiveRunnable, millisecondsDelay);
            }
        }
    }

    private Runnable createRunnable(final Runnable runnable, final long intervalMillis) {
        return new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    runnable.run();
                    handler.postDelayed(this, intervalMillis);
                }
            }
        };
    }

    public void stop() {
        Log.d(TAG, "stop: " + isRunning);
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(repetitiveRunnable);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
