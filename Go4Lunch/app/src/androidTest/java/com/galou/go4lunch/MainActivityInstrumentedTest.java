package com.galou.go4lunch;

/**
 * Created by galou on 2019-04-22
 */

import android.content.Context;
import android.view.Gravity;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.galou.go4lunch.Main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.galou.go4lunch.BottomNavigationItemViewMatcher.withIsChecked;
import static com.galou.go4lunch.BottomNavigationItemViewMatcher.withTitle;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityInstrumentedTest {

    private Context context;

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup(){
        this.context = ApplicationProvider.getApplicationContext();
    }


    @Test
    public void checkNavigationDrawerClose_onPressBack() {
        onView(withId(R.id.main_activity_drawer)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.main_activity_drawer)).check(matches(isClosed(Gravity.LEFT)));
    }

    @Test
    public void clickItemNavDrawer_closeDrawer(){
        onView(withId(R.id.main_activity_drawer)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.main_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.main_activity_drawer_lunch));
        //onView(withId(R.id.main_activity_nav_view)).check(matches(not(isDisplayed())));

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
    public void checkBottomNavigation_buttonSelection(){
        onView(ViewMatchers.withId(R.id.action_map)).check(matches(withIsChecked(true)));
        onView(ViewMatchers.withId(R.id.action_list)).check(matches(withIsChecked(false)));
        onView(ViewMatchers.withId(R.id.action_workmates)).check(matches(withIsChecked(false)));
        onView(ViewMatchers.withId(R.id.action_chat)).check(matches(withIsChecked(false)));
    }

    @Test
    public void checkBottomNavigationButtonTitle(){
        onView(ViewMatchers.withId(R.id.action_map)).check(matches(withTitle(context.getString(R.string.bottom_navigation_menu_map_view))));
        onView(ViewMatchers.withId(R.id.action_list)).check(matches(withTitle(context.getString(R.string.bottom_navigation_menu_list_view))));
        onView(ViewMatchers.withId(R.id.action_workmates)).check(matches(withTitle(context.getString(R.string.bottom_navigation_menu_workmates))));
        onView(ViewMatchers.withId(R.id.action_chat)).check(matches(withTitle(context.getString(R.string.bottom_navigation_menu_chat))));
    }

    @Test
    public void clickMapButton_showMap(){
        onView(withId(R.id.action_map)).perform(click());
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void clickListButton_showList(){
        onView(withId(R.id.action_list)).perform(click());
        onView(withId(R.id.list_view)).check(matches(isDisplayed()));
    }

    @Test
    public void clickWorkmatesButton_showWorkmates(){
        onView(withId(R.id.action_workmates)).perform(click());
        onView(withId(R.id.workmates_view)).check(matches(isDisplayed()));
    }

    @Test
    public void clickChatButton_showChat(){
        onView(withId(R.id.action_chat)).perform(click());
        onView(withId(R.id.chat_view)).check(matches(isDisplayed()));
    }

}