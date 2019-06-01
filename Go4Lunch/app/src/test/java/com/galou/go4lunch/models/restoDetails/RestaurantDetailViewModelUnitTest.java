package com.galou.go4lunch.models.restoDetails;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.restoDetails.RestaurantDetailViewModel;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by galou on 2019-05-31
 */
@RunWith(JUnit4.class)
public class RestaurantDetailViewModelUnitTest {

    private RestaurantDetailViewModel viewModel;

    private User user;

    private String restaurantId;

    private List<Restaurant> restaurants;
    private Restaurant restaurant1;

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
        user = new User("uid", "name", "email", "urlPhoto");
        restaurantId = "12345";
        restaurant1 = new Restaurant("12345", "name", 10.00, 5.00, null, 3, null, 3, "333333333", "www.website");
        restaurants = new ArrayList<>();
        restaurants.add(restaurant1);
        when(userRepository.getUser()).thenReturn(user);
        when(restaurantRepository.getRestaurantSelected()).thenReturn(restaurantId);
        when(restaurantRepository.getRestaurantsLoaded()).thenReturn(restaurants);
        viewModel = new RestaurantDetailViewModel(userRepository, restaurantRepository);
        viewModel.fetchInfoRestaurant();
    }

    /*

    @Test
    public void start_correctInfoRestaurantSetup(){
        assertEquals(restaurant1.getName(), viewModel.nameRestaurant.getValue());
        assertEquals(restaurant1.getAddress(), viewModel.addressRestaurant.getValue());
        assertEquals(restaurant1.getUrlPhoto(), viewModel.urlPhoto.getValue());
        assertEquals((int) restaurant1.getRating(), (int) viewModel.rating.getValue());
        assertEquals(restaurant1.getUsersEatingHere(), viewModel.getUsers().getValue());
        assertNotNull(viewModel.websiteAvailable.getValue());
        assertNotNull(viewModel.phoneAvailable.getValue());
        assertFalse(viewModel.isRestaurantLiked.getValue());
        assertTrue(viewModel.isRestaurantPicked.getValue());
    }
    */
}
