package com.galou.go4lunch.models.main;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.R;
import com.galou.go4lunch.main.MainActivityViewModel;
import com.galou.go4lunch.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.galou.go4lunch.util.UserConverter.convertUserInJson;
import static org.junit.Assert.assertEquals;

/**
 * Created by galou on 2019-05-01
 */
public class MainViewModelUnitTest {

    private MainActivityViewModel viewModel;
    private User user;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup(){
        viewModel = new MainActivityViewModel();
        user = new User("uuid", "username", "email", "urlPhoto");
        viewModel.configureUser(convertUserInJson(user));
    }

    @Test
    public void configureUser_rightUser(){
        assertEquals(user.getUsername(), viewModel.username.getValue());
        assertEquals(user.getEmail(), viewModel.email.getValue());
        assertEquals(user.getUrlPicture(), viewModel.urlPicture.getValue());
    }

    @Test
    public void getUser_returnCorrectUser(){
        assertEquals(user.getUid(), viewModel.getCurrentUser().getUid());
    }

    @Test
    public void logOutRequest_showSnackBar(){
        viewModel.logoutUserFromApp();
        assertEquals((int) viewModel.getSnackBarMessage().getValue(), R.string.logged_out_success);
    }

    @Test
    public void openSetting_openSettingWithUser(){
        viewModel.openSettings();
        assertEquals(convertUserInJson(user), viewModel.getSettings().getValue());
    }
}
