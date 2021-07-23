package com.manuelsoft.mypomodoroapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    private static final String TAG = ClockTest.class.getName();

    @Mock
    Clock.Listener listener = mock(Clock.Listener.class);

    @Test
    public void start_noListenerRegistered_throwException() {
        RealClock realClock = new RealClock(1000L, TimeUnit.MILLISECONDS);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> realClock.start());
        final String message = "Empty listener list";
        String expectedMessage = exception.getMessage();
        assertNotNull(expectedMessage);
        assertThat(expectedMessage.contains(message), is(true));
    }

    @Test
    public void execute_executeClockListener() {
        FakeClock fakeClock = new FakeClock();

        fakeClock.register(listener);

        fakeClock.start();
        assertThat(fakeClock.isRunning(), is(true));

        fakeClock.execute();
        verify(listener, times(1)).execute();

        fakeClock.stop();
        assertThat(fakeClock.isRunning(), is(false));
    }

    @Test
    public void register_aListener_executeListener() throws InterruptedException {
        final List<Integer> a = new ArrayList<>();
        RealClock realClock = new RealClock(500L, TimeUnit.MILLISECONDS);
        realClock.register(() -> a.add(1) );

        realClock.start();
        Thread.sleep(1000L);
        realClock.stop();

        assertThat(a.size() > 0, is(true));
    }

    private class ChronometerTask {
        private int minutes = 20;
        private final Clock clock;

        ChronometerTask (Clock clock) {
            this.clock = clock;
        }

        void decrement() {
            --minutes;
            if (minutes == 0) {
                clock.stop();
            }
        }

        int getMinutes() {
            return minutes;
        }
    }

    @Test
    public void whenDecrement4Times_ThenTimeIsZero() throws InterruptedException {
        RealClock realClock = new RealClock(1L, TimeUnit.MILLISECONDS);
        ChronometerTask chronometerTask = new ChronometerTask(realClock);

        realClock.register(chronometerTask::decrement);

        realClock.start();
        Thread.sleep(20);

        assertThat(chronometerTask.getMinutes(), is(0));
    }


}
