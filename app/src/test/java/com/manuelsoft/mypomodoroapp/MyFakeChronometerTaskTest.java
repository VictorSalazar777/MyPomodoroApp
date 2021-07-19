package com.manuelsoft.mypomodoroapp;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(JUnit4.class)
public class MyFakeChronometerTaskTest {

    private static final String TAG = MyFakeChronometerTaskTest.class.getName();

    interface MyChronometerTask {
        void set(int minutes, Runnable runnable, Runnable end);

        int getMinutes();

        void execute();
    }

    class MyFakeChronometerTask implements MyChronometerTask {

        private int minutes;
        private int seconds;
        private Runnable runnable;
        private Runnable end;

        @Override
        public void set(int minutes, Runnable runnable, Runnable end) {
            this.minutes = minutes;
            this.runnable = runnable;
            this.end = end;
        }

        @Override
        public int getMinutes() {
            return minutes;
        }

        public int getSeconds() {
            return seconds;
        }

        @Override
        public void execute() {
//            int myseconds = 0;
//            int myminutes = minutes;
//            int mycounter = minutes * 60 + 1;
//
//            if (mycounter > 0 && myminutes >= 0) {
//                int aux = mycounter
//                while (mycounter > 0 && myminutes >= 0) {
//                    if (mycounter != aux) {
//                        if (myseconds == 0) {
//                            --myminutes;
//                            myseconds = 59;
//                        } else {
//                            --myseconds;
//                        }
//                    }
//                    --mycounter;
//                }
//
//                this.minutes = myminutes;
//                this.seconds = myseconds;
//            } else {
//                mycounter = -1;
//                this.minutes = 0;
//                this.seconds = 0;
//            }
//            end.run();
        }

        public int getTime (int minutes) {
            return getTime(minutes, 60 * minutes + 1);
        }

        public int getTime (int minutes, int counter) {
            int myseconds = 0;
            int myminutes = minutes;
            int mycounter = counter;

            if (mycounter > 0 && myminutes >= 0) {
                while (mycounter > 0) {
                    if (mycounter != counter) {
                        if (myseconds == 0) {
                            --myminutes;
                            myseconds = 59;
                        } else {
                            --myseconds;
                        }
                    }
                    --mycounter;
                }

                this.minutes = myminutes;
                this.seconds = myseconds;
            } else {
                mycounter = -1;
                this.minutes = 0;
                this.seconds = 0;
            }

            return mycounter;
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
    public void getTime_set20minCounter0_getCounterMinus1AndMinutes0AndSecond0() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(20, 0), is(-1));
        assertThat(myFakeChronometerTask.getMinutes(), is(0));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void getTime_set20minCounter1_getCounter1AndMinutes20AndSecond0() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(20, 1), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(20));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void getTime_set1minCounter61_getCounter0AndMinutes0AndSecond0() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(1, 61), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(0));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void getTime_set20minCounter1201_getCounter0AndMinutes0AndSecond0() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(20, 1201), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(0));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void getTime_set20minCounter101_getCounter0AndMinutes18AndSecond20() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(20, 101), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(18));
        assertThat(myFakeChronometerTask.getSeconds(), is(20));
    }

    @Test
    public void getTime_set20minCounter1_getCounter0AndMinutes19AndSecond59() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(20, 1), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(20));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void getTime_set20minCounter61_getCounter0AndMinutes19AndSecond00() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(20, 61), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(19));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void getTime_set20minDefaultCounter_getCounter0AndMinutes0AndSecond0() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(20), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(0));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void getTime_set15minDefaultCounter_getCounter0AndMinutes0AndSecond0() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.getTime(15), is(0));
        assertThat(myFakeChronometerTask.getMinutes(), is(0));
        assertThat(myFakeChronometerTask.getSeconds(), is(0));
    }

    @Test
    public void print_set1min0sec_getRightString() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.print(1, 0), is("01:00"));
    }

    @Test
    public void print_set1min1sec_getRightString() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.print(1, 1), is("01:01"));
    }

    @Test
    public void print_set1min59sec_getRightString() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.print(1, 59), is("01:59"));
    }

    @Test
    public void print_set10min0sec_getRightString() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.print(10, 0), is("10:00"));
    }

    @Test
    public void print_set10min1sec_getRightString() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.print(10, 1), is("10:01"));
    }

    @Test
    public void print_set10min15sec_getRightString() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
        assertThat(myFakeChronometerTask.print(10, 15), is("10:15"));
    }

    @Test
    @Ignore
    public void execute_set20m() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
       // myFakeChronometerTask.set(20, () -> {});
        myFakeChronometerTask.execute();
    }

    @Test
    @Ignore
    public void execute_set15m() {
        MyFakeChronometerTask myFakeChronometerTask = new MyFakeChronometerTask();
      //  myFakeChronometerTask.set(15, () -> {});
        myFakeChronometerTask.execute();
    }
}
