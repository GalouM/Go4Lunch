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
    private Restaurant restaurant;

    public MutableLiveData<String> nameRestaurant = new MutableLiveData<>();
    public MutableLiveData<String> addressRestaurant = new MutableLiveData<>();
    public MutableLiveData<String> urlPhoto = new MutableLiveData<>();
    public MutableLiveData<Boolean> websiteAvailable = new MutableLiveData<>();
    public MutableLiveData<Boolean> phoneAvailable = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRestaurantLiked = new MutableLiveData<>();

    private MutableLiveData<Integer> rating = new MutableLiveData<>();
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> webSite = new MutableLiveData<>();

    public RestaurantDetailViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void fetchInfoRestaurant(){
        this.restaurant = restaurantRepository.getRestaurantSelected();
        nameRestaurant.setValue(restaurant.getName());
        addressRestaurant.setValue(restaurant.getAddress());
        urlPhoto.setValue(restaurant.getUrlPhoto());
        rating.setValue(restaurant.getRating());
        users.setValue(restaurant.getUsersEatingHere());
        websiteAvailable.setValue(restaurant.getWebSite() != null);
        phoneAvailable.setValue(restaurant.getPhoneNumber() != null);
        isRestaurantLiked.setValue(checkIfRestaurantIsLiked());
    }

    public void fetchPhoneNumber(){
        phoneNumber.setValue(restaurant.getPhoneNumber());
    }

    public void fetchWebsite(){
        webSite.setValue(restaurant.getWebSite());
    }

    private Boolean checkIfRestaurantIsLiked(){
        List<String> restaurantLiked = userRepository.getUser().getLikedRestaurantUuid();
        if(restaurantLiked != null && restaurantLiked.size() > 0) {
            for (String uid : restaurantLiked) {
                if (uid.equals(restaurant.getUid())) return true;
            }
        }

        return false;

    }

    @Override
    public void retry(RetryAction retryAction) {

    }
}
