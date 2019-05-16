package com.galou.go4lunch.repositories;

import com.galou.go4lunch.api.GooglePlaceService;
import com.galou.go4lunch.models.ApiDetailResponse;
import com.galou.go4lunch.models.ApiNearByResponse;
import com.galou.go4lunch.models.DistanceApiResponse;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.ResultApiPlace;
import com.galou.go4lunch.models.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by galou on 2019-05-08
 */
public class RestaurantRepository {

    private final GooglePlaceService googlePlaceService;

    private List<Restaurant> restaurants;
    private String restaurantSelected;
    private String location;

    private static volatile RestaurantRepository INSTANCE;

    public static RestaurantRepository getInstance(GooglePlaceService googlePlaceService){
        if(INSTANCE == null){
            INSTANCE = new RestaurantRepository(googlePlaceService);
        }
        return INSTANCE;
    }

    private RestaurantRepository(GooglePlaceService googlePlaceService){
        this.googlePlaceService = googlePlaceService;
    }

    public Observable<ApiNearByResponse> streamFetchRestaurantsNearBy(String location){
        return googlePlaceService.getRestaurantsNearBy(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<ApiDetailResponse> streamFetchRestaurantDetails(String placeId){
        return googlePlaceService.getRestaurantDetail(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<List<ApiDetailResponse>> streamFetchListRestaurantDetails(){
        return streamFetchRestaurantsNearBy(this.location)
                .map(ApiNearByResponse::getResults)
                .concatMap((Function<List<ResultApiPlace>, Observable<List<ApiDetailResponse>>>) results -> Observable.fromIterable(results)
                        .concatMap((Function<ResultApiPlace, Observable<ApiDetailResponse>>) result -> streamFetchRestaurantDetails(result.getPlaceId()))
                        .toList()
                        .toObservable());
    }

    public Observable<DistanceApiResponse> getDistanceToPoint(String point){
        return googlePlaceService.getDistancePoints(this.location, point)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public String getPhotoRestaurant(String photoReference){
        return String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=AIzaSyAsyZraxhl3hj1_bjYCPLVHbMgd6s62mxc", photoReference);
    }

    public List<Restaurant> getRestaurantsLoaded(){
        return restaurants;
    }

    public void updateRestaurants(List<Restaurant> restaurants){
        this.restaurants = restaurants;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRestaurantSelected(String restaurantUid){
        this.restaurantSelected = restaurantUid;
    }

    public String getRestaurantSelected(){
        return restaurantSelected;
    }

    public void setUserEatingRestaurant(int position, User user){
        restaurants.get(position).addUser(user);
    }


}
