package com.galou.go4lunch.repositories;

import com.galou.go4lunch.api.GooglePlaceService;
import com.galou.go4lunch.models.ApiResponse;

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

    public RestaurantRepository(GooglePlaceService googlePlaceService){
        this.googlePlaceService = googlePlaceService;
    }

    public Observable<ApiResponse> getRestaurantsNearBy(String location, Integer radius){
        return googlePlaceService.getRestaurantsNearBy(location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
