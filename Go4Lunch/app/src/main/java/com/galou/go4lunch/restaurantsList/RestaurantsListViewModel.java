package com.galou.go4lunch.restaurantsList;

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
import com.galou.go4lunch.util.OpeningHoursUtil;
import com.galou.go4lunch.util.RatingUtil;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
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

    private RestaurantRepository restaurantRepository;
    private Disposable disposableRestaurant;
    private Disposable disposableDistance;
    private List<Restaurant> restaurants;
    private List<User> users;

    //----- PRIVATE LIVE DATA -----
    private MutableLiveData<List<Restaurant>> restaurantsList = new MutableLiveData<>();
    private MutableLiveData<Object> openDetailRestaurant = new MutableLiveData<>();

    //----- GETTER LIVE DATA -----
    public LiveData<List<Restaurant>> getRestaurantsList(){
        return restaurantsList;
    }

    public LiveData<Object> getOpenDetailRestaurant() {
        return openDetailRestaurant;
    }

    public RestaurantsListViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // --------------------
    // START
    // --------------------

    public void setupLocation(String location){
        restaurantRepository.setLocation(location);

    }

    // --------------------
    // GET USER ACTION
    // --------------------

    public void requestListRestaurants(){
        isLoading.setValue(true);
        if(restaurantRepository.getRestaurantsLoaded() == null || restaurantRepository.getRestaurantsLoaded().size() == 0) {
            this.fetchListRestaurant();
        } else {
            restaurantsList.setValue(restaurantRepository.getRestaurantsLoaded());
            isLoading.setValue(false);
        }

    }

    public void updateRestaurantSelected(String restaurantUid){
        restaurantRepository.setRestaurantSelected(restaurantUid);
        openDetailRestaurant.setValue(new Object());
    }

    public void onRefreshRestaurantListList(){
        isLoading.setValue(true);
        this.fetchListRestaurant();
    }

    @Override
    public void retry(RetryAction retryAction) {
        if (retryAction == GET_RESTAURANTS) {
            this.requestListRestaurants();
        }
    }

    public void destroyDisposable(){
        if (this.disposableRestaurant != null && !this.disposableRestaurant.isDisposed()) this.disposableRestaurant.dispose();
        if (this.disposableDistance != null && !this.disposableDistance.isDisposed()) this.disposableDistance.dispose();
    }

    // --------------------
    // OBSERVER API
    // --------------------
    private DisposableObserver<List<ApiDetailResponse>> getObserverRestaurants(){
        return new DisposableObserver<List<ApiDetailResponse>>() {
            @Override
            public void onNext(List<ApiDetailResponse> response) {
                createRestaurantList(response);
            }

            @Override
            public void onError(Throwable e) {
                snackBarWithAction.setValue(GET_RESTAURANTS);
                isLoading.setValue(false);

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

            }

            @Override
            public void onComplete() {

            }
        };
    }

    // --------------------
    // SETUP RESTAURANT LIST
    // --------------------
    private void createRestaurantList(List<ApiDetailResponse> results){
        restaurants = new ArrayList<>();
        for (ApiDetailResponse detailResult : results){
            if(detailResult.getResult() != null) {
                Restaurant restaurant = restaurantRepository.createRestaurant(detailResult.getResult());
                restaurants.add(restaurant);
                LatLng positionRestaurant = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                this.disposableDistance = restaurantRepository.getDistanceToPoint(convertLocationForApi(positionRestaurant)).subscribeWith(getObserverDistance(restaurant));
            }

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
        for (User user : users) {
            if (user.getRestaurantUid() != null) {
                String restaurantPicked = user.getRestaurantUid();
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
        isLoading.setValue(false);


    }

    private void fetchListRestaurant(){
        if(restaurantRepository.getLocation() != null) {
            this.disposableRestaurant = restaurantRepository.streamFetchListRestaurantDetails().subscribeWith(getObserverRestaurants());
        } else {
            snackBarText.setValue(R.string.no_location_message);
            isLoading.setValue(false);
        }
    }

}
