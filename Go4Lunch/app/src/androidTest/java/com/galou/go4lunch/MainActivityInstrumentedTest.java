package com.galou.go4lunch;

/**
 * Created by galou on 2019-04-22
 */

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.galou.go4lunch.main.MainActivity;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.galou.go4lunch.BottomNavigationItemViewMatcher.withTitle;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityInstrumentedTest {

    private User user;

    private UserRepository userRepository;

    private Context context;

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        user = new User("uid", "name", "email", "urlPhoto");
        user.setRestaurantUid("restoUid");
        user.setRestaurantName("name Resto");
        user.setRestaurantAddress("123 address");
        userRepository = UserRepository.getInstance();
        userRepository.createUser(user.getUid(), user.getUsername(), user.getEmail(), null);
        userRepository.updateRestaurantPicked(user.getRestaurantUid(), user.getRestaurantName(), user.getRestaurantAddress(), user.getUid());
        this.context = ApplicationProvider.getApplicationContext();
        mainActivityTestRule.launchActivity(new Intent());

    }


    @Test
    public void checkNavigationDrawerClose_onPressBack() {
        onView(withId(R.id.drawer_view)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.drawer_view)).check(matches(isClosed(Gravity.LEFT)));
    }

    @Test
    public void clickItemNavDrawer_closeDrawer(){
        onView(withId(R.id.drawer_view)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());;

    }

    @Test
    public void bottomNavigation_visible(){
        onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()));
    }

    @Test
    public void toolBar_visible(){
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void checkBottomNavigationButtonTitle(){
        onView(ViewMatchers.withId(R.id.action_map)).check(matches(withTitle(context.getString(R.string.bottom_navigation_menu_map_view))));
        onView(ViewMatchers.withId(R.id.action_list)).check(matches(withTitle(context.getString(R.string.bottom_navigation_menu_list_view))));
        onView(ViewMatchers.withId(R.id.action_workmates)).check(matches(withTitle(context.getString(R.string.bottom_navigation_menu_workmates))));
    }

    @Test
    public void clickMapButton_showMap(){
        onView(withId(R.id.action_map)).perform(click());
        onView(withId(R.id.mapView)).check(matches(isDisplayed()));
    }

    @Test
    public void clickListButton_showList(){
        onView(withId(R.id.action_list)).perform(click());
        onView(withId(R.id.recycler_view_resto)).check(matches(isDisplayed()));
    }


    @Test
    public void clickWorkmatesButton_showWorkmates(){
        onView(withId(R.id.action_workmates)).perform(click());
        onView(withId(R.id.frame_layout_workmates)).check(matches(isDisplayed()));
    }

    @Test
    public void clickSettings_openSettingsActivity(){
        onView(withId(R.id.drawer_view)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.main_activity_drawer_settings));

        onView(withId(R.id.activity_setting_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void clickLunch_openDetailRestaurantActivity(){
        onView(withId(R.id.drawer_view)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.main_activity_drawer_lunch));

        onView(withId(R.id.detail_restaurant_rootView)).check(matches(isDisplayed()));
    }

    @Test
    public void clickLogout_ShowMainActivity(){
        onView(withId(R.id.drawer_view)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.main_activity_drawer_logout));
        //waitForNetworkCall();

        //onView(withId(R.id.auth_activity_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void checkNavHeaderInfo_equalsUserInfo(){
        onView(withId(R.id.drawer_view)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_header_username)).check(matches(withText(user.getUsername())));
        onView(withId(R.id.nav_header_email)).check(matches(withText(user.getEmail())));
    }


}
