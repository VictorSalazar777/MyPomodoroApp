package com.manuelsoft.mypomodoroapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class MyChronometerTaskTest {

    public static final int FIFTEEN = 15;
    public static final int TWENTY = 20;
    private boolean run = false;

    interface MyTask {
        void execute(int minutes, int seconds, long counter);
    }

    static class MyChronometerTaskForTesting {

        private int minutes = 0;
        private int seconds = -1;
        private long counter = 0;
        private MyTask myTask;
        private MyTask end;
        private MyCountDownTimer myCountDownTimer;
        boolean init = false;

        public void set(int minutes, MyTask myTask, MyTask end) {
            set(minutes, 0, myTask, end);
        }

        public void set(int minutes, int seconds, MyTask myTask, MyTask end) {
            this.minutes = minutes;
            this.myTask = myTask;
            this.end = end;
            this.seconds = seconds;
            counter = 0L;
        }

        public void cancel() {
            myCountDownTimer.cancel();
        }

        public void execute() {
            init = true;
            myCountDownTimer = new MyCountDownTimer(minutes * 60000L + seconds * 1000L + 1000L, 1000L) {
                @Override
                void onFinish() {
                    end.execute(minutes, seconds, counter);
                }

                @Override
                void onTick(long millisUntilFinished) {
                    if (millisUntilFinished <= 0L) {
                        this.cancel();
                    }
                    counter++;
                    if (init) {
                        init = false;
                    } else if (seconds == 0) {
                        --minutes;
                        seconds = 59;
                    } else {
                        --seconds;
                    }
                    myTask.execute(minutes, seconds, counter);
                }
            };
            myCountDownTimer.start();
        }

        public String print(int minutes, int seconds) {
            String chronometerString;
            String secondsString, minutesString;

            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = String.valueOf(seconds);
            }

            if (minutes < 10) {
                minutesString = "0" + minutes;
            } else {
                minutesString = String.valueOf(minutes);
            }

            chronometerString = minutesString + ":" + secondsString;
            return chronometerString;
        }
    }

    @Test
    public void onTick_input20min_return19minutes59secondsInTheSecondIteration() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {
            if (counter == 2L) {
                run = true;
                assertThat(minutes, is(19));
                assertThat(seconds, is(59));
                myChronometerTask.cancel();
            }
        };

        MyTask end = (minutes, seconds, counter) -> {

        };

        myChronometerTask.set(TWENTY, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));

    }

    @Test
    public void onTick_input20min_return20minutes0secondsInTheFirstIteration() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {
            if (counter == 1L) {
                run = true;
                myChronometerTask.cancel();
                assertThat(minutes, is(TWENTY));
                assertThat(seconds, is(0));
            }
        };

        MyTask end = (minutes, seconds, counter) -> {

        };

        myChronometerTask.set(TWENTY, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));


    }

    @Test
    public void onTick_input20min_return0minutes0secondsInTheLastIteration() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {
            if (counter == 1201L) {
                run = true;
                myChronometerTask.cancel();
                assertThat(minutes, is(0));
                assertThat(seconds, is(0));
            }
        };

        MyTask end = (minutes, seconds, counter) -> {

        };

        myChronometerTask.set(TWENTY, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));


    }

    @Test
    public void onTick_input15min_return0minutes0secondsInTheLastIteration() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {
            if (counter == 901L) {
                run = true;
                myChronometerTask.cancel();
                assertThat(minutes, is(0));
                assertThat(seconds, is(0));
            }
        };

        MyTask end = (minutes, seconds, counter) -> {

        };

        myChronometerTask.set(FIFTEEN, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));


    }

    @Test
    public void onFinish_input20min_return0minutes0seconds() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {

        };

        MyTask end = (minutes, seconds, counter) -> {
            run = true;
            assertThat(minutes, is(0));
            assertThat(seconds, is(0));
        };

        myChronometerTask.set(TWENTY, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));
    }

    @Test
    public void onFinish_input15min_return0minutes0seconds() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {

        };

        MyTask end = (minutes, seconds, counter) -> {
            run = true;
            assertThat(minutes, is(0));
            assertThat(seconds, is(0));
        };

        myChronometerTask.set(FIFTEEN, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));

    }


    @Test
    public void print_input20min_return0minutes0seconds() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {

        };

        MyTask end = (minutes, seconds, counter) -> {
            run = true;
            String time = myChronometerTask.print(minutes, seconds);
            assertThat(time, is("00:00"));
        };

        myChronometerTask.set(TWENTY, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));
    }


    @Test
    public void print_input20min_return20minutes0secondsInTheFirstIteration() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {
            if (counter == 1L) {
                run = true;
                String time = myChronometerTask.print(minutes, seconds);
                assertThat(time, is("20:00"));
            }
        };

        MyTask end = (minutes, seconds, counter) -> {

        };

        myChronometerTask.set(TWENTY, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));
    }

    @Test
    public void onTick_input20min_reRun20minutes0secondsInTheFirstIteration() {
        MyChronometerTaskForTesting myChronometerTask = new MyChronometerTaskForTesting();

        MyTask myTask = (minutes, seconds, counter) -> {
            if (counter == 1L) {
                run = true;
                String time = myChronometerTask.print(minutes, seconds);
                assertThat(time, is("20:00"));
            }
        };

        MyTask end = (minutes, seconds, counter) -> {

        };

        myChronometerTask.set(TWENTY, myTask, end);
        myChronometerTask.execute();

        assertThat(run, is(true));
    }


}
