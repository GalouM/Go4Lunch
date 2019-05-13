package com.galou.go4lunch.restaurantsList;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.ApiDetailResponse;
import com.galou.go4lunch.models.DistanceApiResponse;
import com.galou.go4lunch.models.ElementApiDistance;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.ResultApiPlace;
import com.galou.go4lunch.models.RowApiDistance;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.galou.go4lunch.util.PositionUtil.convertLocationForApi;
import static com.galou.go4lunch.util.RetryAction.GET_RESTAURANTS;

/**
 * Created by galou on 2019-05-08
 */
public class RestaurantsListViewModel extends BaseViewModel {

    private MutableLiveData<List<Restaurant>> restaurantsList = new MutableLiveData<>();


    private RestaurantRepository restaurantRepository;
    private Disposable disposableRestaurant;
    private Disposable disposableDistance;
    private Disposable disposablePhoto;
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
                this.disposableRestaurant = restaurantRepository.streamFetchListRestaurantDetails(location, RADIUS_SEARCH).subscribeWith(getObserverRestaurants());
            } else {
                snackBarText.setValue(R.string.no_location_message);
            }
        } else {
            restaurantsList.setValue(restaurantRepository.getRestaurantsLoaded());
        }

    }

    private DisposableObserver<List<ApiDetailResponse>> getObserverRestaurants(){
        return new DisposableObserver<List<ApiDetailResponse>>() {
            @Override
            public void onNext(List<ApiDetailResponse> response) {
                createRestaurantList(response);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("error", e.toString());
                snackBarWithAction.setValue(GET_RESTAURANTS);

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private DisposableObserver<DistanceApiResponse> getObserverDistance(Restaurant restaurant){
        return new DisposableObserver<DistanceApiResponse>() {
            @Override
            public void onNext(DistanceApiResponse distanceApi) {
                List<RowApiDistance> row = distanceApi.getRows();
                if(row.size() > 0){
                    List<ElementApiDistance> elements = row.get(0).getElements();
                    if(elements.size() > 0){
                        Integer distance = elements.get(0).getDistance().getValue();
                        restaurant.setDistance(distance);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("error distance", e.toString());

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void createRestaurantList(List<ApiDetailResponse> results){
        restaurants = new ArrayList<>();
        for (ApiDetailResponse detailResult : results){
            ResultApiPlace result = detailResult.getResult();
            String uid = result.getId();
            String name = result.getName();
            Double latitude = result.getGeometry().getLocation().getLat();
            Double longitude = result.getGeometry().getLocation().getLng();
            String photo = restaurantRepository.getPhotoRestaurant(result.getPhotos().get(0).getPhotoReference());
            String address = result.getVicinity();
            String openingHours = null;
            String closureHours = null;
            if(result.getOpeningHours() != null){
                if(result.getOpeningHours().getPeriods() != null){
                    if(result.getOpeningHours().getPeriods().size() > 0){
                        if(result.getOpeningHours().getPeriods().get(0) != null) {
                            if (result.getOpeningHours().getPeriods().get(0).getOpen() != null)
                                openingHours = result.getOpeningHours().getPeriods().get(0).getOpen().getTime();
                        }
                            if(result.getOpeningHours().getPeriods().get(0).getClose() != null) {
                                closureHours = result.getOpeningHours().getPeriods().get(0).getClose().getTime();
                            }

                    }
                }
            }


            Restaurant restaurant = new Restaurant(uid, name, latitude, longitude, address, openingHours, closureHours, photo);
            restaurants.add(restaurant);
            LatLng positionRestaurant = new LatLng(latitude, longitude);
            this.disposableDistance = restaurantRepository.getDistanceToPoint(location, convertLocationForApi(positionRestaurant)).subscribeWith(getObserverDistance(restaurant));

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
