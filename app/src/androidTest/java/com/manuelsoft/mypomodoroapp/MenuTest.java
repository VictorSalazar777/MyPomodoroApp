package com.manuelsoft.mypomodoroapp;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.manuelsoft.mypomodoroapp.ui.main.MainActivity;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.manuelsoft.mypomodoroapp.ui.main.MainActivity.UI_SHARED_PREFERENCES;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MenuTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityTestRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @After
    public void finish() {
        activityTestRule.getScenario().onActivity(MainActivity::destroyService);
        cleanUISharedPreferences();
    }

    private void cleanUISharedPreferences() {
        ApplicationProvider.getApplicationContext()
                .getSharedPreferences(UI_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    @Test
    public void overflowMenuOptions() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.txt_menu_item_credits)).check(matches(isDisplayed()));
        if (BuildConfig.DEBUG) {
            onView(withText(R.string.txt_menu_item_one_tick_test)).check(matches(isDisplayed()));
            onView(withText(R.string.txt_menu_item_5_sec_test)).check(matches(isDisplayed()));
            onView(withText(R.string.txt_menu_item_reset_tests)).check(matches(isDisplayed()));
        }
    }

}
