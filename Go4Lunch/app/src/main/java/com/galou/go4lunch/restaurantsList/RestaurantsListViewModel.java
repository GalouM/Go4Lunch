package com.galou.go4lunch.restaurantsList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.ApiResponse;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.Result;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.RetryAction;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.galou.go4lunch.util.RetryAction.GET_RESTAURANTS;

/**
 * Created by galou on 2019-05-08
 */
public class RestaurantsListViewModel extends BaseViewModel {

    private MutableLiveData<List<Restaurant>> restaurantsList = new MutableLiveData<>();


    private RestaurantRepository restaurantRepository;
    private Disposable disposable;
    private String location;
    private List<Restaurant> restaurants;
    private List<User> users;
    private final int RADIUS_SEARCH = 1500;

    public LiveData<List<Restaurant>> getRestaurantsList(){
        return restaurantsList;
    }

    public RestaurantsListViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void setupLocation(String location){
        this.location = location;

    }

    public void requestListRestaurants(){
        if(restaurantRepository.getRestaurantsLoaded() == null) {
            if(location != null) {
                this.disposable = restaurantRepository.getRestaurantsNearBy(location, RADIUS_SEARCH).subscribeWith(getObserverRestaurants());
            } else {
                snackBarText.setValue(R.string.no_location_message);
            }
        } else {
            restaurantsList.setValue(restaurantRepository.getRestaurantsLoaded());
        }

    }

    private DisposableObserver<ApiResponse> getObserverRestaurants(){
        return new DisposableObserver<ApiResponse>() {
            @Override
            public void onNext(ApiResponse response) {
                List<Result> restaurants = response.getResults();
                createRestaurantList(restaurants);
            }

            @Override
            public void onError(Throwable e) {
                snackBarWithAction.setValue(GET_RESTAURANTS);

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void createRestaurantList(List<Result> results){
        restaurants = new ArrayList<>();
        for (Result result : results){
            String uid = result.getId();
            String name = result.getName();
            Double latitude = result.getGeometry().getLocation().getLat();
            Double longitude = result.getGeometry().getLocation().getLng();
            String type = null;
            String address = result.getVicinity();
            String openingHours = null;
            String distance = null;
            String urlPhoto = null;
            Restaurant restaurant = new Restaurant(uid, name, latitude, longitude, type, address, openingHours, distance, urlPhoto);
            restaurants.add(restaurant);

        }
        this.fetchListUser();
    }

    private void fetchListUser(){
        users = new ArrayList<>();
        userRepository.getAllUsersFromFirebase()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        User user = documentSnapshot.toObject(User.class);
                        users.add(user);
                    }
                    this.checkUserRestaurant();
        });

    }

    private void checkUserRestaurant(){
        for(User user : users){
            if(user.getRestaurantPicked() != null) {
                String restaurantPicked = user.getRestaurantPicked().getUid();
                for (Restaurant restaurant : restaurants) {
                    String restaurantUid = restaurant.getUid();
                    if (restaurantUid.equals(restaurantPicked)) {
                        restaurant.addUser(user);
                    }
                }
            }
        }
        restaurantsList.setValue(restaurants);
        restaurantRepository.updateRestaurants(restaurants);
    }



    @Override
    public void retry(RetryAction retryAction) {
        if (retryAction == GET_RESTAURANTS) {
            this.requestListRestaurants();
        }

    }
}
