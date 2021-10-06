package com.manuelsoft.mypomodoroapp;

import android.view.View;
import android.widget.Chronometer;

import androidx.test.espresso.matcher.BoundedMatcher;

import com.manuelsoft.mypomodoroapp.ui.main.ChronometerView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.is;

/**
 *  A custom matcher that checks the isTheFinalCountDown property of a {@link Chronometer}.
 *  It accepts a {@link Matcher}.
 */
public class MyChronometerActiveMatcher {

    public static final String TAG = MyChronometerActiveMatcher.class.getName();

    static Matcher<View> withIsActive(final Boolean isActive) {
         return withIsActive(is(isActive));

/*
        return new BoundedMatcher<View, Chronometer>(Chronometer.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with isTheFinalCountDown: ");
            }

            @Override
            protected boolean matchesSafely(Chronometer item) {
                return item != null && isTheFinalCountDown == item.isTheFinalCountDown();
            }
        };
*/
    }

    static Matcher<View> withIsActive(final Matcher<Boolean> booleanMatcher) {
        if (booleanMatcher == null) {
            throw new IllegalArgumentException("Null value");
        }

        return new BoundedMatcher<>(ChronometerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with isTheFinalCountDown: ");
                booleanMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(ChronometerView item) {
                return booleanMatcher.matches(item.isActive());
            }
        };
    }
}
