package com.manuelsoft.mypomodoroapp;

import com.manuelsoft.mypomodoroapp.ui.main.MainActivityPresenter;
import com.manuelsoft.mypomodoroapp.ui.main.MainActivityPresenterImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith(JUnit4.class)
public class MainActivityPresenterTest {

    MainActivityPresenter mainActivityPresenter;

    @Before
    public void setup() {
        mainActivityPresenter = new MainActivityPresenterImpl();
    }

    @Test
    public void setStateActive_newStateActive() {
        mainActivityPresenter.setStateActive();
        assertThat(mainActivityPresenter.isActive(), is(true));
    }

    @Test
    public void setStateInactive_newStateInactive() {
        mainActivityPresenter.setStateInactive();
        assertThat(!mainActivityPresenter.isActive(), is(true));
    }

    @Test
    public void setFifteenMinutes_getFifteenMinutes() {
        final int MINUTES = 15;
        mainActivityPresenter.setFifteenMinutes();
        assertThat(mainActivityPresenter.getHowManyMinutes(), is(MINUTES));
    }

    @Test
    public void setTwentyMinutes_getTwentyMinutes() {
        final int MINUTES = 20;
        mainActivityPresenter.setTwentyMinutes();
        assertThat(mainActivityPresenter.getHowManyMinutes(), is(MINUTES));
    }
}
