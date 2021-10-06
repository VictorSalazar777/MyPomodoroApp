package com.manuelsoft.mypomodoroapp.ui.main;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ChronometerView extends AppCompatTextView {

    private boolean isRunning = false;

    public ChronometerView(@NonNull Context context) {
        super(context);
    }

    public ChronometerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChronometerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isActive() {
        return isRunning;
    }

    public void setActive(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
