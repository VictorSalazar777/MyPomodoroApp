package com.manuelsoft.mypomodoroapp.chronometer;


import android.util.Log;

public class ChronometerTimer {

    public static final String TAG = ChronometerTimer.class.getName();
    private ChronometerTask task;
    private ChronometerTask end;
    private int minutes;
    private int seconds;
    private long counter = 0;
    private RepetitiveTask repetitiveTask;

    public void set(int minutes, ChronometerTask task, ChronometerTask end) {
        set(minutes, 0, task, end);
    }

    public void set(int minutes, int seconds, ChronometerTask task, ChronometerTask end) {
        assert task != null : "Task is null";
        assert end != null : "End is null";
        this.task = task;
        this.end = end;
        this.minutes = minutes;
        this.seconds = seconds;
        counter = minutes * 60L + seconds;
        repetitiveTask = new RepetitiveTask(createChronometerRunnable(), 1000L);
    }

    public void execute() {
        repetitiveTask.start(true);
    }

    private Runnable createChronometerRunnable() {
        return () -> {
            Log.d(TAG, "createChronometerRunnable: " + counter + " s: " + seconds);

            if (counter == 0) {
                task.execute(minutes, seconds, counter);
                end.execute(minutes, seconds, counter);
                Log.d(TAG, "createChronometerRunnable: stop");
                repetitiveTask.stop();
            }
            task.execute(minutes, seconds, counter);
            if (seconds == 0) {
                --minutes;
                seconds = 59;
            } else {
                --seconds;
            }
            --counter;
        };
    }

    public void cancel() {
        repetitiveTask.stop();
    }

}
