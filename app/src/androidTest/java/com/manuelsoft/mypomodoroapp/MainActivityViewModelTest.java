package com.manuelsoft.mypomodoroapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.manuelsoft.mypomodoroapp.ui.main.MainActivityViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class MainActivityViewModelTest {
    private MainActivityViewModel mainActivityViewModel;

    @Before
    public void setup() {
        mainActivityViewModel = new MainActivityViewModel();
    }

    @Test
    public void setStateActive_newStateActive() {
        mainActivityViewModel.setStateActive();
        assertThat(mainActivityViewModel.isActive(), is(true));
    }

    @Test
    public void setStateInactive_newStateInactive() {
        mainActivityViewModel.setStateInactive();
        assertThat(!mainActivityViewModel.isActive(), is(true));
    }

    @Test
    public void setFifteenMinutes_getFifteenMinutes() {
        final int MINUTES = 15;
        mainActivityViewModel.setFifteenMinutes();
        assertThat(mainActivityViewModel.getHowManyMinutes(), is(MINUTES));
    }

    @Test
    public void setTwentyMinutes_getTwentyMinutes() {
        final int MINUTES = 20;
        mainActivityViewModel.setTwentyMinutes();
        assertThat(mainActivityViewModel.getHowManyMinutes(), is(MINUTES));
    }
}