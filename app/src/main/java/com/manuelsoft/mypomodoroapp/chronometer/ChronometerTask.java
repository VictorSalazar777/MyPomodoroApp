package com.manuelsoft.mypomodoroapp.chronometer;

public interface ChronometerTask {
    void execute(int minutes, int seconds, long counter);
}