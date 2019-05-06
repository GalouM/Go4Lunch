package com.galou.go4lunch.models.main;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.R;
import com.galou.go4lunch.main.MainActivityViewModel;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by galou on 2019-05-01
 */
public class MainViewModelUnitTest {

    @Mock
    private MainActivityViewModel viewModel;
    private User user;

    @Mock
    private FirebaseAuth firebaseAuth;
    private CountDownLatch authSignal = null;
    @Mock
    private Application context;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getResources()).thenReturn(mock(Resources.class));
        FirebaseApp.initializeApp(context);
        when(firebaseAuth.getInstance().getCurrentUser().getUid()).thenReturn("uuid");
        viewModel = new MainActivityViewModel();
        viewModel.configureUser();
    }

    @Test
    public void configureUser_rightUser(){
        assertEquals(user.getUsername(), viewModel.username.getValue());
        assertEquals(user.getEmail(), viewModel.email.getValue());
        assertEquals(user.getUrlPicture(), viewModel.urlPicture.getValue());
    }

    @Test
    public void logOutRequest_showSnackBar(){
        viewModel.logoutUserFromApp();
        assertEquals((int) viewModel.getSnackBarMessage().getValue(), R.string.logged_out_success);
    }

    @Test
    public void openSetting_openSettingWithUser(){
        viewModel.openSettings();
        //assertEquals(convertUserInJson(user), viewModel.getSettings().getValue());
    }
}
