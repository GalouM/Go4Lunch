package com.galou.go4lunch;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingResource;
import androidx.test.rule.ActivityTestRule;

import com.galou.go4lunch.main.MainActivity;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by galou on 2019-05-31
 */
public class ListViewFragmentInstrumentedTest {

    private Context context;
    private IdlingResource idlingResource;

    private User user;

    private UserRepository userRepository;;

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void setup(){
        user = new User("uid", "name", "email", "urlPhoto");
        user.setRestaurantUid("restoUid");
        user.setRestaurantName("name Resto");
        user.setRestaurantAddress("123 address");
        userRepository = UserRepository.getInstance();
        userRepository.createUser(user.getUid(), user.getUsername(), user.getEmail(), null);
        userRepository.updateRestaurantPicked(user.getRestaurantUid(), user.getRestaurantName(), user.getRestaurantAddress(), user.getUid());
        this.context = ApplicationProvider.getApplicationContext();
        mainActivityTestRule.launchActivity(new Intent());
        onView(withId(R.id.action_list)).perform(click());


    }

    @Test
    public void recyclerView_isDisplayed(){
        onView(withId(R.id.recycler_view_resto)).check(matches(isDisplayed()));
    }
}
