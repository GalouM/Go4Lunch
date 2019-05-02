package com.galou.go4lunch.models.settings;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.models.User;
import com.galou.go4lunch.settings.SettingsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static com.galou.go4lunch.util.UserConverter.convertUserInJson;
import static org.junit.Assert.assertEquals;

/**
 * Created by galou on 2019-05-01
 */
@RunWith(JUnit4.class)
public class SettingViewModelUnitTest {

    private SettingsViewModel viewModel;
    private User user;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


    @Before
    public void setup(){
        viewModel = new SettingsViewModel();
        user = new User("uuid", "username", "email", "urlPhoto");
        viewModel.configureUser(convertUserInJson(user));
    }

    @Test
    public void userInfo_correct(){
        assertEquals(user.getUsername(), viewModel.username.getValue());
        assertEquals(user.getEmail(), viewModel.email.getValue());
        assertEquals(user.getUrlPicture(), viewModel.urlPicture.getValue());
    }
}
