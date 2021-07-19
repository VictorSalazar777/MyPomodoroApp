package com.manuelsoft.mypomodoroapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static com.manuelsoft.mypomodoroapp.MainActivityPresenter.TWENTY;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private MainActivityPresenter mainActivityPresenter;
    private Button btnStartStop;
    private Button btnFifteenMinutes;
    private Button btnTwentyMinutes;
    private MyChronometer chronometer;
    private MyChronometerTask myChronometerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityPresenter = new MainActivityPresenterImpl();
        mainActivityPresenter.setStateInactive();
        setupChronometer();
        setupStartStopBtn();
        setupFifteenMinutesBtn();
        setupTwentyMinutesBtn();

    }

    private void setupTwentyMinutesBtn() {
        btnTwentyMinutes = findViewById(R.id.btn_twenty_min);
        btnTwentyMinutes.setEnabled(false);
        btnTwentyMinutes.setOnClickListener(v -> {
            if (!mainActivityPresenter.isActive()) {
                btnTwentyMinutes.setEnabled(false);
                btnFifteenMinutes.setEnabled(true);
                mainActivityPresenter.setTwentyMinutes();
                chronometer.setText(R.string.txt_twenty_minutes);
            }
        });
    }

    private void setupFifteenMinutesBtn() {
        btnFifteenMinutes = findViewById(R.id.btn_fifteen_min);
        btnFifteenMinutes.setEnabled(true);
        btnFifteenMinutes.setOnClickListener(v -> {
            if (!mainActivityPresenter.isActive()) {
                btnTwentyMinutes.setEnabled(true);
                btnFifteenMinutes.setEnabled(false);
                mainActivityPresenter.setFifteenMinutes();
                chronometer.setText(R.string.txt_fifteen_minutes);
            }
        });
    }

    private void setupStartStopBtn() {
        btnStartStop = findViewById(R.id.btn_start_stop);
        btnStartStop.setOnClickListener(v -> {
           if (mainActivityPresenter.isActive()) {
               mainActivityPresenter.setStateInactive();
               btnStartStop.setText(R.string.txt_btn_start);
               if (mainActivityPresenter.getHowManyMinutes() == TWENTY) {
                   btnFifteenMinutes.setEnabled(true);
               } else {
                   btnTwentyMinutes.setEnabled(true);
               }
               myChronometerTask.cancel();
           } else {
               mainActivityPresenter.setStateActive();
               btnStartStop.setText(R.string.txt_btn_stop);
               btnFifteenMinutes.setEnabled(false);
               btnTwentyMinutes.setEnabled(false);
               registerChronometerTask();
               myChronometerTask.execute();
           }
        });
    }

    private void setupChronometer() {
        chronometer = findViewById(R.id.chronometer);
        myChronometerTask = new MyChronometerTask();
        chronometer.setMyChronometerTask(myChronometerTask);
        String minutes = mainActivityPresenter.getHowManyMinutes() + ":00";
        chronometer.setText(minutes);
    }

    private void registerChronometerTask() {
        MyTask myTask = (minutes, seconds, counter) -> {
            String time = myChronometerTask.print(minutes, seconds);
            chronometer.setText(time);
        };

        MyTask end = (minutes, seconds, counter) -> {
            //Toast.makeText(this, "Pomodoro finished!", Toast.LENGTH_LONG).show();
            mainActivityPresenter.setStateInactive();
            btnStartStop.setText(R.string.txt_btn_start);
            if (mainActivityPresenter.getHowManyMinutes() == TWENTY) {
                btnFifteenMinutes.setEnabled(true);
            } else {
                btnTwentyMinutes.setEnabled(true);
            }
        };
        myChronometerTask.set(mainActivityPresenter.getHowManyMinutes(), myTask, end);
    }



}