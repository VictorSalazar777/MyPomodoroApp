package com.manuelsoft.mypomodoroapp;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.content.ContextCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ServiceTestRule;

import com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class MyChronometerServiceTest {

    @Rule
    public final ServiceTestRule serviceTestRule = new ServiceTestRule();

    @Test
    public void serviceIsStartedCorrectly() throws TimeoutException {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        getApplicationContext().startService(intent);

        IBinder binder = serviceTestRule.bindService(intent);
        MyChronometerService service = ((MyChronometerService.MyChronometerBinder) binder).getService();

        assertThat(service.isActive(), is(true));
    }

    @Test
    public void startChronometer_chronometerIsStartedCorrectly() throws TimeoutException, InterruptedException {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        getApplicationContext().startService(intent);

        IBinder binder = serviceTestRule.bindService(intent);
        MyChronometerService service = ((MyChronometerService.MyChronometerBinder) binder).getService();

        service.setChronometer(20);
        service.startChronometer();

        Thread.sleep(100);

        assertThat(service.chronometerIsActive(), is(true));
    }

    @Test
    public void stopChronometer_chronometerIsFinishedCorrectly() throws TimeoutException, InterruptedException {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        getApplicationContext().startService(intent);

        IBinder binder = serviceTestRule.bindService(intent);
        MyChronometerService service = ((MyChronometerService.MyChronometerBinder) binder).getService();

        service.setChronometer(20);
        service.startChronometer();

        Thread.sleep(100);

        service.stopChronometer();

        assertThat(service.chronometerIsActive(), is(false));
    }

    @Test
    public void startForegroundService() {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        ComponentName name;

        if (Build.VERSION.SDK_INT >= 26) {
            name = getApplicationContext().startForegroundService(intent);
        } else {
            name = getApplicationContext().startService(intent);
        }

        assertThat(name, notNullValue());
    }


}
