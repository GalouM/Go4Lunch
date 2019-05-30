package com.galou.go4lunch.models.restaurantsList;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.R;
import com.galou.go4lunch.main.MainActivityViewModel;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.restaurantsList.RestaurantsListViewModel;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by galou on 2019-05-29
 */
@RunWith(JUnit4.class)
public class RestaurantListViewModelUnitTest {

    private RestaurantsListViewModel viewModel;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    private LatLng location;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        location = new LatLng(123.456, -654.321);
        when(restaurantRepository.getLocation()).thenReturn(location);
        viewModel = new RestaurantsListViewModel(userRepository, restaurantRepository);
        viewModel.checkIfLocationIsAvailable();
    }

    @Test
    public void start_emitLocationUser(){
        assertEquals(location, viewModel.getLocationUser().getValue());
    }

    @Test
    public void noLocationSetRequestLocation(){
        when(restaurantRepository.getLocation()).thenReturn(null);
        viewModel.checkIfLocationIsAvailable();
        assertNotNull(viewModel.getRequestLocation().getValue().getContentIfNotHandle());
    }

    @Test
    public void noLocation_showSnackBar(){
        viewModel.noLocationAvailable();
        assertEquals((int) viewModel.getSnackBarMessage().getValue().getContentIfNotHandle(), R.string.no_location_message);
    }

    @Test
    public void setupLocation_emitLocationAndSetupRepo(){
        viewModel.setupLocation(location);
        assertEquals(location, viewModel.getLocationUser().getValue());
        verify(restaurantRepository).setLocation(location);
    }
}
