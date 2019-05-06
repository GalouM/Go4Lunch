package com.galou.go4lunch;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.galou.go4lunch.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by galou on 2019-05-01
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class WorkmatesFragmentInstrumentedTest {

    private Context context;
    private IdlingResource idlingResource;

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup(){
        this.context = ApplicationProvider.getApplicationContext();
        onView(withId(R.id.action_workmates)).perform(click());

    }

    @Test
    public void recyclerView_isDisplayed(){
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
    }
}
