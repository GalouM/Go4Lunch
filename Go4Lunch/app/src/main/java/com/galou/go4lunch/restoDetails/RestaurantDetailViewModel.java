package com.galou.go4lunch.restoDetails;

import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.RetryAction;

import java.util.List;

/**
 * Created by galou on 2019-05-14
 */
public class RestaurantDetailViewModel extends BaseViewModel {

    private RestaurantRepository restaurantRepository;

    public MutableLiveData<String> nameRestaurant = new MutableLiveData<>();
    public MutableLiveData<String> addressRestaurant = new MutableLiveData<>();
    public MutableLiveData<String> urlPhoto = new MutableLiveData<>();

    private MutableLiveData<Integer> rating = new MutableLiveData<>();
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> webSite = new MutableLiveData<>();

    public RestaurantDetailViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void fetchInfoRestaurant(int position){
        Restaurant restaurant = restaurantRepository.getRestaurantsLoaded().get(position);
        nameRestaurant.setValue(restaurant.getName());
        addressRestaurant.setValue(restaurant.getAddress());
        urlPhoto.setValue(restaurant.getUrlPhoto());
        rating.setValue(restaurant.getRating());
        users.setValue(restaurant.getUsersEatingHere());
    }

    @Override
    public void retry(RetryAction retryAction) {

    }
}
