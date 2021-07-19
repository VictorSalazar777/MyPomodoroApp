package com.manuelsoft.mypomodoroapp;

import java.util.concurrent.TimeUnit;

public interface Clock {
    void register(Listener listener);
    void start();
    void stop();
    void set(long period, TimeUnit periodTimeUnit);
    void set(long period, long initialDelay, TimeUnit periodTimeUnit);
    void clean();
    boolean isRunning();
    interface Listener {
        void execute();
    }
}
