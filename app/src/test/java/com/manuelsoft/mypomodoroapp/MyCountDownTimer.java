package com.manuelsoft.mypomodoroapp;

public abstract class MyCountDownTimer  {

    private boolean cancelled = false;

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long millisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long countdownInterval;

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        this.millisInFuture = millisInFuture;
        countdownInterval = countDownInterval;
    }

    public final void cancel() {
        cancelled = true;
    }

    abstract void onFinish();

    abstract void onTick(long millisUntilFinished);

    final void start() {
        cancelled = false;
        if (millisInFuture <= 0) {
            onFinish();
        } else {
            handler();
        }
    }

    private void handler() {
        long counter = millisInFuture / countdownInterval;
        long millisUntilFinished = millisInFuture;
        while (counter > 0) {
            if (cancelled) {
                return;
            }
            counter--;
            millisUntilFinished -= countdownInterval;
            onTick(millisUntilFinished);
        }
        onFinish();
    }

}
