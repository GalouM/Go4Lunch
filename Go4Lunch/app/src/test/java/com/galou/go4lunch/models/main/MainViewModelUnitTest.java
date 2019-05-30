package com.galou.go4lunch.models.main;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.R;
import com.galou.go4lunch.main.MainActivityViewModel;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.SaveDataRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;
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

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by galou on 2019-05-01
 */
public class MainViewModelUnitTest {

    private MainActivityViewModel viewModel;
    private User user;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private SaveDataRepository saveDataRepository;
    @Mock
    private Context context;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User("uid", "name", "email", "urlPhoto");
        when(userRepository.getUser()).thenReturn(user);
        when(saveDataRepository.getNotificationSettings(user.getUid())).thenReturn(true);
        when(saveDataRepository.getPreferences()).thenReturn(null);
        viewModel = new MainActivityViewModel(userRepository, restaurantRepository, saveDataRepository);
        viewModel.configureInfoUser();
    }

    @Test
    public void configureUser_rightUser(){
        assertEquals(user.getUsername(), viewModel.username.getValue());
        assertEquals(user.getEmail(), viewModel.email.getValue());
        assertEquals(user.getUrlPicture(), viewModel.urlPicture.getValue());
        assertTrue(viewModel.getIsNotificationEnable().getValue());
        verify(saveDataRepository).saveUserId(user.getUid());
    }

    @Test
    public void configureSaveRepo_configureContext(){
        viewModel.configureSaveDataRepo(context);
        verify(saveDataRepository).configureContext(context);

    }

    @Test
    public void logOutRequest_showSnackBar(){
        viewModel.logoutUserFromApp();
        assertEquals((int) viewModel.getSnackBarMessage().getValue().getContentIfNotHandle(), R.string.logged_out_success);
        assertNotNull(viewModel.getLogout().getValue().getContentIfNotHandle());
    }

    @Test
    public void clickSetting_openSettings(){
        viewModel.openSettings();
        assertNotNull(viewModel.getSettings().getValue().getContentIfNotHandle());
    }

    @Test
    public void userNoRestaurantPickedClickYourLunch_showSnackBar(){
        viewModel.showUserRestaurant();
        assertEquals((int) viewModel.getSnackBarMessage().getValue().getContentIfNotHandle(), R.string.no_restaurant_picked_message);
        assertNull(viewModel.getOpenDetailRestaurant().getValue());
    }

    @Test
    public void userRestaurantPickedClickYourLunch_showSnackBar(){
        user.setRestaurantUid("12345");
        viewModel.showUserRestaurant();
        verify(restaurantRepository).setRestaurantSelected(user.getRestaurantUid());
        assertNull(viewModel.getSnackBarMessage().getValue());
        assertNotNull(viewModel.getOpenDetailRestaurant().getValue().getContentIfNotHandle());
    }

    @Test
    public void configureLocation_setupLocation(){
        LatLng location = new LatLng(-123.4223, 334.3234);
        viewModel.configureLocationUser(location);
        assertEquals(location, viewModel.getLocation().getValue());
        verify(restaurantRepository).setLocation(location);
    }

    @Test
    public void noLocation_showSnackBar(){
        viewModel.noLocationAvailable();
        assertEquals((int) viewModel.getSnackBarMessage().getValue().getContentIfNotHandle(), R.string.no_location_message);
    }

    @Test
    public void selectRestaurant_showDetailRestaurant(){
        String uid = "12345";
        viewModel.showRestaurantSelected(uid);
        verify(restaurantRepository).setRestaurantSelected(uid);
        assertNotNull(viewModel.getOpenDetailRestaurant().getValue().getContentIfNotHandle());
    }
}
