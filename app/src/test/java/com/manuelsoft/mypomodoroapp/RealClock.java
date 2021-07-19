package com.manuelsoft.mypomodoroapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealClock implements Clock {
    private final List<Listener> listeners = Collections.synchronizedList(new ArrayList<>());
    private long period;
    private long initialDelay;
    private TimeUnit periodTimeUnit;
    private final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();
    private boolean isRunning = false;

    public RealClock(long period, TimeUnit periodTimeUnit) {
        this(period, 0L, periodTimeUnit);
    }

    public RealClock(long period, long initialDelay, TimeUnit periodTimeUnit) {
        this.period = period;
        this.initialDelay = initialDelay;
        this.periodTimeUnit = periodTimeUnit;
    }

    @Override
    public void register(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void start() {
        if (listeners.isEmpty()) {
            throw new RuntimeException("Empty listener list");
        }
        isRunning = true;
        timerService.scheduleAtFixedRate(this::execute, initialDelay, period, periodTimeUnit);
    }

    private void execute() {
        for (Listener l : listeners) {
            l.execute();
        }
    }

    @Override
    public void stop() {
        isRunning = false;
        timerService.shutdown();
    }

    @Override
    public void set(long period, TimeUnit periodTimeUnit) {
        this.period = period;
        this.initialDelay = 0L;
        this.periodTimeUnit = periodTimeUnit;
    }

    @Override
    public void set(long period, long initialDelay, TimeUnit periodTimeUnit) {
        this.period = period;
        this.initialDelay = initialDelay;
        this.periodTimeUnit = periodTimeUnit;
    }

    @Override
    public void clean() {
        listeners.clear();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
