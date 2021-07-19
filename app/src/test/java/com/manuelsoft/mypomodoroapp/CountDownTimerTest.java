package com.manuelsoft.mypomodoroapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CountDownTimerTest {

    private static final String TAG = CountDownTimerTest.class.getName();

    private final long millisInFuture;
    private final long countDownInterval;
    private long millis;

    public CountDownTimerTest(long millisInFuture, long countDownInterval) {
        super();
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
    }

    @Parameters
    public static Collection<Object[]> input() {
        return Arrays.asList(new Object[][]
                {{60000L, 1000L}, {900000L,1000L}, {1200000L, 1000L}}
                );
    }

    @Test
    public void start_return0() {
        new MyCountDownTimer(millisInFuture, countDownInterval) {
            @Override
            void onFinish() {

            }

            @Override
            void onTick(long millisUntilFinished) {
                millis = millisUntilFinished;
                if (millis <= 0L) {
                    this.cancel();
                }
            }
        }.start();

        assertThat(millis, is(0L));
    }

}
