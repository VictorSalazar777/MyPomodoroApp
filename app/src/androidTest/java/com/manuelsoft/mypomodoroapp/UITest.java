package com.manuelsoft.mypomodoroapp;

import android.os.SystemClock;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {

    public static final String TAG = UITest.class.getName();
    private static final String TWENTY_MINUTES = "20:00";
    private static final String FIFTEEN_MINUTES = "15:00";

    @Rule
    public ActivityScenarioRule<MainActivity> activityTestRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void clickStartStopBtn_onBtnShowingStartText_showStopText() {
        Log.d(TAG, "Checking initial state");
        onView(withId((R.id.btn_start_stop)))
                .check(matches(withText(R.string.txt_btn_start)));

        Log.d(TAG, "Clicking btn_start_stop");
        onView(withId(R.id.btn_start_stop)).perform(click())
                .check(matches(withText(R.string.txt_btn_stop)));

    }

    @Test
    public void clickStartStopBtn_onBtnShowingStopText_showStartText() {
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking initial state");
        onView(withId((R.id.btn_start_stop)))
                .check(matches(withText(R.string.txt_btn_stop)));

        Log.d(TAG, "Clicking btn_start_stop");
        onView(withId((R.id.btn_start_stop))).perform(click())
                .check(matches(withText(R.string.txt_btn_start)));
    }

    @Test
    public void clickStartStopBtn_onBtnShowingStartText_startChronometer() {

        Log.d(TAG, "Checking initial state");
        onView(withId((R.id.btn_start_stop)))
                .check(matches(withText(R.string.txt_btn_start)));

        Log.d(TAG, "Clicking btn_start_stop");
        onView(withId(R.id.btn_start_stop)).perform(click());
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(true)));
    }

    @Test
    public void clickStartStopBtn_onBtnShowingStopText_stopChronometer() {

        Log.d(TAG, "Clicking btn_start_stop");
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking initial state");
        onView(withId((R.id.btn_start_stop)))
                .check(matches(withText(R.string.txt_btn_stop)));

        Log.d(TAG, "Clicking btn_start_stop");
        onView(withId(R.id.btn_start_stop)).perform(click());
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));
    }

    @Test
    public void FifteenMinutesBtnSays_15min () {
        onView(withId(R.id.btn_fifteen_min)).check(matches(withText(R.string.txt_btn_fifteen_minutes)));
    }

    @Test
    public void TwentyMinutesBtnSays_20min () {
        onView(withId(R.id.btn_twenty_min)).check(matches(withText(R.string.txt_btn_twenty_minutes)));
    }

    @Test
    public void GivenStatusInactive_WhenClickStartStopBtn_ThenFifteenMinutesBtnAndTwentyMinutesBtnAreDisabled() {
        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));

        Log.d(TAG,  "Clicking start_stop button");
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking buttons states");
        onView(withId(R.id.btn_fifteen_min)).check(matches(isNotEnabled()));
        onView(withId(R.id.btn_twenty_min)).check(matches(isNotEnabled()));
    }

    @Test
    public void GivenStatusActiveWithFifteenMinutes_WhenClickStartStopBtn_ThenTwentyMinutesBtnIsEnabled() {
        onView(withId(R.id.btn_fifteen_min)).perform(click());
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(true)));

        Log.d(TAG,  "Clicking start_stop button");
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking button state");
        onView(withId(R.id.btn_twenty_min)).check(matches(isEnabled()));
    }

    @Test
    public void GivenStatusActiveWithTwentyMinutes_WhenClickStartStopBtn_ThenFifteenMinutesBtnIsEnabled() {
        onView(withId(R.id.btn_twenty_min)).perform(click());
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(true)));

        Log.d(TAG,  "Clicking start_stop button");
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking button state");
        onView(withId(R.id.btn_fifteen_min)).check(matches(isEnabled()));
    }

    @Test
    public void GivenStatusActiveWithFifteenMinutes_WhenClickStartStopBtn_ThenFifteenMinutesIsShowed() {
        onView(withId(R.id.btn_fifteen_min)).perform(click());
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(true)));

        SystemClock.sleep(1000);

        Log.d(TAG,  "Clicking start_stop button");
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking button state");
        onView(withId(R.id.chronometer)).check(matches(withText(FIFTEEN_MINUTES)));
    }

    @Test
    public void GivenStatusActiveWithTwentyMinutes_WhenClickStartStopBtn_ThenTwentyMinutesIsShowed() {
        onView(withId(R.id.btn_twenty_min)).perform(click());
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(true)));

        SystemClock.sleep(1000);

        Log.d(TAG,  "Clicking start_stop button");
        onView(withId(R.id.btn_start_stop)).perform(click());

        Log.d(TAG, "Checking button state");
        onView(withId(R.id.chronometer)).check(matches(withText(TWENTY_MINUTES)));
    }

    @Test
    public void GivenTheFirstRunning_ThenStatusInactiveAndChronometerShowsTwentyMinutesAndTwentyMinutesBtnDisabledAndFifteenMinutesBtnEnabled() {
        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));

        Log.d(TAG, "Checking first running requirements");
        onView(withId(R.id.chronometer)).check(matches(withText(TWENTY_MINUTES)));
        onView(withId(R.id.btn_twenty_min)).check(matches(isNotEnabled()));
        onView(withId(R.id.btn_fifteen_min)).check(matches(isEnabled()));
    }

    @Test
    public void GivenStatusInactive_WhenClickTwentyMinutesBtn_ThenChronometerShowsTwentyMinutes() {
        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));

        Log.d(TAG,  "Clicking twenty button");
        onView(withId(R.id.btn_twenty_min)).perform(click());

        Log.d(TAG,  "Checking chronometer text");
        onView(withId(R.id.chronometer)).check(matches(withText(TWENTY_MINUTES)));
    }

    @Test
    public void GivenStatusInactive_WhenClickFifteenMinutesBtn_ThenChronometerShowsFifteenMinutes() {
        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));

        Log.d(TAG,  "Clicking twenty button");
        onView(withId(R.id.btn_fifteen_min)).perform(click());

        Log.d(TAG,  "Checking chronometer text");
        onView(withId(R.id.chronometer)).check(matches(withText(FIFTEEN_MINUTES)));
    }

    @Test
    public void GivenStatusInactiveAfterClicksOnFifteenMinutesBtn_WhenClickTwentyMinutesBtn_ThenFifteenMinutesBtnIsEnabledAndTwentyMinutesBtnIsDisabled() {
        Log.d(TAG,  "Clicking fifteen button");
        onView(withId(R.id.btn_fifteen_min)).perform(click());

        Log.d(TAG, "Checking required state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));

        Log.d(TAG,  "Clicking twenty button");
        onView(withId(R.id.btn_twenty_min)).perform(click());

        Log.d(TAG,  "Checking if FifteenMinutesBtn is disabled");
        onView(withId(R.id.btn_fifteen_min)).check(matches(isEnabled()));

        Log.d(TAG,  "Checking if TwentyMinutesBtn is enabled");
        onView(withId(R.id.btn_twenty_min)).check(matches(isNotEnabled()));

    }

    @Test
    public void GivenStatusInactive_WhenClickFifteenMinutesBtn_ThenFifteenMinutesBtnIsDisabledAndTwentyMinutesBtnIsEnabled() {
        Log.d(TAG, "Checking initial state");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));

        Log.d(TAG,  "Clicking fifteen button");
        onView(withId(R.id.btn_fifteen_min)).perform(click());

        Log.d(TAG,  "Checking if FifteenMinutesBtn is disabled");
        onView(withId(R.id.btn_fifteen_min)).check(matches(isNotEnabled()));

        Log.d(TAG,  "Checking if TwentyMinutesBtn is enabled");
        onView(withId(R.id.btn_twenty_min)).check(matches(isEnabled()));

    }

    @Test
    public void WhenPomodoroTimeIsUp_ThenShowPomodoroFinishDialog() {
        Log.d(TAG,  "Clicking test button");
        onView(withId(R.id.btn_test)).perform(click());

        SystemClock.sleep(1000);

        Log.d(TAG,  "Checking if Pomodoro finished dialog is showed");
        onView(withId(android.R.id.button3)).check(matches(isDisplayed()));

    }

    @Test
    public void WhenPomodoroFinishedDialogBtnIsClicked_ThenPomodoroTimeIsReestablished() {
        Log.d(TAG,  "Clicking test button");
        onView(withId(R.id.btn_test)).perform(click());

        SystemClock.sleep(1000);

        Log.d(TAG,  "Clicking on Pomodoro dialog button");
        onView(withId(android.R.id.button3)).perform(click());

        Log.d(TAG,  "Checking if Pomodoro finished dialog is gone");
        onView(withId(android.R.id.button3)).check(doesNotExist());

        Log.d(TAG, "Checking if chronometer state is inactive");
        onView(withId(R.id.chronometer))
                .check(matches(MyChronometerActiveMatcher.withIsActive(false)));

    }


}