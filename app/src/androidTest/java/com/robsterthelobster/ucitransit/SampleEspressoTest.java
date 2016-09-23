package com.robsterthelobster.ucitransit;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.robsterthelobster.ucitransit.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by robin on 9/20/2016.
 */

@RunWith(AndroidJUnit4.class)
public class SampleEspressoTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void validateEditText() {
//        onView(withId(R.id.hello))
//                .perform(typeText("Hello"))
//                .check(matches(withText("Hello")));
    }
}
