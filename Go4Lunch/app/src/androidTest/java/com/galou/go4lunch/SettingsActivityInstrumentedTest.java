package com.galou.go4lunch;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.settings.SettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by galou on 2019-04-30
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class SettingsActivityInstrumentedTest {

    private User user;

    private UserRepository userRepository;

    private Context context;
    private IdlingResource idlingResource;

    @Rule
    public final ActivityTestRule<SettingsActivity> settingsActivityTestRule = new ActivityTestRule<>(SettingsActivity.class, false, false);

    @Before
    public void setup(){
        user = new User("uid", "name", "email@email.com", "urlPhoto");
        userRepository = UserRepository.getInstance();
        userRepository.createUser(user.getUid(), user.getUsername(), user.getEmail(), null);
        this.context = ApplicationProvider.getApplicationContext();
        settingsActivityTestRule.launchActivity(new Intent());

    }

    @Test
    public void tooShortUserName_showError(){
        onView(withId(R.id.username_field)).perform(replaceText("GA"));
        onView(withId(R.id.update_button)).perform(closeSoftKeyboard(), scrollTo(), click());
        onView(withText(R.string.incorrect_username)).check(matches(isDisplayed()));
    }

    @Test
    public void missingAtEmail_showError(){
        onView(withId(R.id.email_field)).perform(replaceText("email"));
        onView(withId(R.id.update_button)).perform(closeSoftKeyboard(), scrollTo(), click());
        onView(withText(R.string.incorrect_email)).check(matches(isDisplayed()));
    }

    @Test
    public void missingDomainExtEmail_showError(){
        onView(withId(R.id.email_field)).perform(replaceText("email@email"));
        onView(withId(R.id.update_button)).perform(closeSoftKeyboard(), scrollTo(), click());
        onView(withText(R.string.incorrect_email)).check(matches(isDisplayed()));
    }

    @Test
    public void correctUserInfo_noErrorAndShowSnack(){
        onView(withId(R.id.username_field)).perform(replaceText("User Name"));
        onView(withId(R.id.email_field)).perform(replaceText("email@email.com"));
        onView(withId(R.id.update_button)).perform(closeSoftKeyboard(), scrollTo(), click());
        onView(withText(R.string.incorrect_username)).check(doesNotExist());
        onView(withText(R.string.incorrect_email)).check(doesNotExist());
        //waitForNetworkCall();
        //onView(withText(R.string.settings_update_user_information)).check(matches(isDisplayed()));
    }

    private void waitForNetworkCall(){
        this.idlingResource = settingsActivityTestRule.getActivity().getEspressoIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

}
