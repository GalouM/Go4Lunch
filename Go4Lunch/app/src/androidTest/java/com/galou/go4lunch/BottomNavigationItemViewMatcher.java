package com.galou.go4lunch;

import android.view.View;

import androidx.test.espresso.matcher.BoundedMatcher;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by galou on 2019-04-22
 */
public final class BottomNavigationItemViewMatcher {

    public static Matcher<View> withIsChecked(final boolean isChecked){
        return new BoundedMatcher<View, BottomNavigationItemView>(BottomNavigationItemView.class) {

            private boolean triedMatching;
            @Override
            protected boolean matchesSafely(BottomNavigationItemView item) {
                triedMatching = true;
                return item.getItemData().isChecked() == isChecked;
            }

            @Override
            public void describeTo(Description description) {
                if(triedMatching) {
                    description.appendText("with isChecked: " + String.valueOf(isChecked));
                    description.appendText("But was: " + String.valueOf(!isChecked));
                }

            }
        };
    }

    public static Matcher<View> withTitle(final String titleTested){
        return new BoundedMatcher<View, BottomNavigationItemView>(BottomNavigationItemView.class) {

            private boolean triedMatching;
            private String title;

            @Override
            protected boolean matchesSafely(BottomNavigationItemView item) {
                this.triedMatching = true;
                this.title = item.getItemData().getTitle().toString();
                return title.equals(titleTested);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with title: " + titleTested);
                description.appendText("But was: " + String.valueOf(title));

            }
        };
    }
}
