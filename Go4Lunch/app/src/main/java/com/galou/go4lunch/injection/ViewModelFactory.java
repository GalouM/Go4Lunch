package com.galou.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.galou.go4lunch.restaurantsList.RestaurantsListViewModel;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.authentication.AuthenticationViewModel;
import com.galou.go4lunch.main.MainActivityViewModel;
import com.galou.go4lunch.restoDetails.RestaurantDetailViewModel;
import com.galou.go4lunch.settings.SettingsViewModel;
import com.galou.go4lunch.workmates.WorkmatesViewModel;

/**
 * Created by galou on 2019-05-02
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public ViewModelFactory(UserRepository userRepository, RestaurantRepository restaurantRepository){
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(AuthenticationViewModel.class)){
            return (T) new AuthenticationViewModel(userRepository);
        }
        if(modelClass.isAssignableFrom(MainActivityViewModel.class)){
            return (T) new MainActivityViewModel(userRepository, restaurantRepository);
        }
        if(modelClass.isAssignableFrom(SettingsViewModel.class)){
            return (T) new SettingsViewModel(userRepository);
        }
        if(modelClass.isAssignableFrom(WorkmatesViewModel.class)){
            return (T) new WorkmatesViewModel(userRepository, restaurantRepository);
        }
        if(modelClass.isAssignableFrom(RestaurantsListViewModel.class)){
            return (T) new RestaurantsListViewModel(userRepository, restaurantRepository);
        }
        if(modelClass.isAssignableFrom(RestaurantDetailViewModel.class)){
            return (T) new RestaurantDetailViewModel(userRepository, restaurantRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
