package com.galou.go4lunch.injection;

import android.util.Log;

import com.galou.go4lunch.api.GooglePlaceService;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;

/**
 * Created by galou on 2019-05-06
 */
public class Injection {

    public static UserRepository provideUserRepository(){
        return UserRepository.getInstance();
    }

    public static RestaurantRepository provideRestaurantRepository() {
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return RestaurantRepository.getInstance(googlePlaceService);
    }

    public static ViewModelFactory provideViewModelFactory(){
        UserRepository userRepository = provideUserRepository();
        RestaurantRepository restaurantRepository = provideRestaurantRepository();

        return new ViewModelFactory(userRepository, restaurantRepository);
    }
}
