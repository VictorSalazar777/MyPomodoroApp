package com.manuelsoft.mypomodoroapp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FakeClock implements Clock {

    private final List<Listener> listeners = new ArrayList<>();
    private boolean isRunning = false;

    @Override
    public void register(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void start() {
        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public void set(long period, TimeUnit periodTimeUnit) {

    }

    @Override
    public void set(long period, long initialDelay, TimeUnit periodTimeUnit) {

    }

    @Override
    public void clean() {
        listeners.clear();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public void execute() {
        for (Listener l : listeners) {
            l.execute();
        }
    }
}
