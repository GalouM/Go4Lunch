package com.galou.go4lunch.repositories;

import android.util.Log;

import com.galou.go4lunch.api.GooglePlaceService;
import com.galou.go4lunch.models.ApiResponse;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.Result;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by galou on 2019-05-08
 */
public class RestaurantRepository {

    private final GooglePlaceService googlePlaceService;

    private List<Restaurant> restaurants;

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

    public Observable<ApiResponse> getRestaurantsNearBy(String location, Integer radius){
        return googlePlaceService.getRestaurantsNearBy(location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public List<Restaurant> getRestaurantsLoaded(){
        return restaurants;
    }

    public void updateRestaurants(List<Restaurant> restaurants){
        this.restaurants = restaurants;
    }
}
