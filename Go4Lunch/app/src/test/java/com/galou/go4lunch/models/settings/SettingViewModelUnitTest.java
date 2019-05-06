package com.galou.go4lunch.models.settings;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.UserRepository;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by galou on 2019-05-01
 */
@RunWith(JUnit4.class)
public class SettingViewModelUnitTest {

    private SettingsViewModel viewModel;
    private User user;

    @Mock
    private UserRepository userRepository;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        user = new User("uid", "name", "email", "urlPhoto");
        when(userRepository.getUser()).thenReturn(user);
        viewModel = new SettingsViewModel(userRepository);
        viewModel.configureUser();
    }

    @Test
    public void userInfo_correct(){
        assertEquals(user.getUsername(), viewModel.username.getValue());
        assertEquals(user.getEmail(), viewModel.email.getValue());
        assertEquals(user.getUrlPicture(), viewModel.urlPicture.getValue());
    }

    @Test
    public void disableNotification_disable(){
        viewModel.notificationStateChanged(false);
        assertFalse(viewModel.isNotificationEnabled.getValue());
        assertEquals((int) viewModel.getSnackBarMessage().getValue(), R.string.notification_disabled);
    }

    @Test
    public void enableNotification_enable(){
        viewModel.notificationStateChanged(true);
        assertTrue(viewModel.isNotificationEnabled.getValue());
        assertEquals((int) viewModel.getSnackBarMessage().getValue(), R.string.notifications_enabled);
    }

    @Test
    public void deleteUserRequest_openDialog(){
        viewModel.deleteUserFromDBRequest();
        //assertEquals(new Object(), viewModel.getOpenDialog().getValue());
    }
}
