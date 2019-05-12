package com.galou.go4lunch.restaurantsList;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.ApiResponse;
import com.galou.go4lunch.models.DistanceApi;
import com.galou.go4lunch.models.Element;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.Result;
import com.galou.go4lunch.models.Row;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
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
                this.disposableRestaurant = restaurantRepository.getRestaurantsNearBy(location, RADIUS_SEARCH).subscribeWith(getObserverRestaurants());
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

    private DisposableObserver<DistanceApi> getObserverDistance(Restaurant restaurant){
        return new DisposableObserver<DistanceApi>() {
            @Override
            public void onNext(DistanceApi distanceApi) {
                List<Row> row = distanceApi.getRows();
                if(row.size() > 0){
                    List<Element> elements = row.get(0).getElements();
                    if(elements.size() > 0){
                        Integer distance = elements.get(0).getDistance().getValue();
                        restaurant.setDistance(distance);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private DisposableObserver<String> getObserverPhoto(Restaurant restaurant){
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String urlPhoto) {
                Log.e("here", "here");
                //restaurant.setUrlPhoto(urlPhoto);
                Log.e("urlphoto", urlPhoto.toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("error", e.toString());

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
            Restaurant restaurant = new Restaurant(uid, name, latitude, longitude, type, address, openingHours);
            restaurants.add(restaurant);
            Log.e("photo", String.valueOf(result.getPhotos().size()));
            if(result.getPhotos().size() > 0) {
                String photoReference = result.getPhotos().get(0).getPhotoReference();
                Log.e("ref", photoReference);
                this.disposablePhoto = restaurantRepository.getPhotoRestaurant(photoReference).subscribeWith(getObserverPhoto(restaurant));
            }
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
