package com.manuelsoft.mypomodoroapp;

import android.os.CountDownTimer;

interface MyTask {
    void execute(int minutes, int seconds, long counter);
}

public class MyChronometerTask {

    private int minutes = 0;
    private int seconds = -1;
    private long counter = 0L;
    private MyTask myTask;
    private MyTask end;
    private CountDownTimer myCountDownTimer;
    private boolean isRunning = false;

    public void set(int minutes, MyTask myTask, MyTask end) {
        this.minutes = minutes;
        this.myTask = myTask;
        this.end = end;
        this.seconds = -1;
        counter = 0L;
    }

    public void cancel() {
        isRunning = false;
        myCountDownTimer.cancel();
    }

    public CountDownTimer execute() {
        isRunning = true;
        myCountDownTimer = new CountDownTimer(minutes * 60000L + 1000L, 1000L) {
            @Override
            public void onFinish() {
                counter++;
                seconds--;
                myTask.execute(minutes, seconds, counter);
                end.execute(minutes, seconds, counter);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished <= 0L) {
                    cancel();
                } else {
                    counter++;
                    if (seconds == -1) {
                        seconds = 0;
                    } else if (seconds == 0) {
                        --minutes;
                        seconds = 59;
                    } else {
                        --seconds;
                    }
                    myTask.execute(minutes, seconds, counter);
                }
            }
        };
        myCountDownTimer.start();
        return myCountDownTimer;
    }

    public String print(int minutes, int seconds) {
        String chronometerString;
        String secondsString, minutesString;

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = String.valueOf(seconds);
        }

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = String.valueOf(minutes);
        }

        chronometerString = minutesString + ":" + secondsString;
        return chronometerString;
    }

   // @MainThread
    public boolean isRunning() {
        return isRunning;
    }
}
