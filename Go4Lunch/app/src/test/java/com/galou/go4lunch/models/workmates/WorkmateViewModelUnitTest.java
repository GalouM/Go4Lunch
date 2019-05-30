package com.galou.go4lunch.models.workmates;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.workmates.WorkmatesViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by galou on 2019-05-29
 */
@RunWith(JUnit4.class)
public class WorkmateViewModelUnitTest {

    private WorkmatesViewModel viewModel;

    private User user;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User("uid", "name", "email", "urlPhoto");
        when(userRepository.getUser()).thenReturn(user);
        viewModel = new WorkmatesViewModel(userRepository, restaurantRepository);
    }

    @Test
    public void openDetailRestaurant_openDetailAndUpdateRepo(){
        User userSelected = new User("uid", "name", "email", "urlPhoto");
        userSelected.setRestaurantUid("12345");
        viewModel.updateRestaurantToDisplay(userSelected);
        verify(restaurantRepository).setRestaurantSelected(userSelected.getRestaurantUid());
        assertNotNull(viewModel.getOpenDetailRestaurant().getValue().getContentIfNotHandle());
    }

    @Test
    public void userNoRestaurantSetup_noAction(){
        User userSelected = new User("uid", "name", "email", "urlPhoto");
        viewModel.updateRestaurantToDisplay(userSelected);
        assertNull(viewModel.getOpenDetailRestaurant().getValue());
    }

}
