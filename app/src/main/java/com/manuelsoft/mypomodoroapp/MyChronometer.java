package com.manuelsoft.mypomodoroapp;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class MyChronometer extends AppCompatTextView {

    private MyChronometerTask myChronometerTask;

    public MyChronometer(@NonNull Context context) {
        super(context);
    }

    public MyChronometer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyChronometer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void start() throws RuntimeException {
        myChronometerTask.execute();
    }

    public void stop() {
        myChronometerTask.cancel();
    }

    public boolean isActive() {
        return myChronometerTask.isRunning();
    }

    public void setMyChronometerTask(MyChronometerTask myChronometerTask) {
        this.myChronometerTask = myChronometerTask;
    }
}
