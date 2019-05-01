package com.galou.go4lunch;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.galou.go4lunch.settings.SettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Created by galou on 2019-04-30
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class SettingsActivityInstrumentedTest {

    private Context context;

    @Rule
    public final ActivityTestRule<SettingsActivity> mainActivityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    @Before
    public void setup(){
        this.context = ApplicationProvider.getApplicationContext();
    }

}
