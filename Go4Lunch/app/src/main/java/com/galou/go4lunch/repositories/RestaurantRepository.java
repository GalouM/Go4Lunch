package com.galou.go4lunch.repositories;

import android.location.Location;

import com.galou.go4lunch.BuildConfig;
import com.galou.go4lunch.api.GooglePlaceService;
import com.galou.go4lunch.models.ApiDetailResponse;
import com.galou.go4lunch.models.ApiNearByResponse;
import com.galou.go4lunch.models.DistanceApiResponse;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.ResultApiPlace;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.OpeningHoursUtil;
import com.galou.go4lunch.util.PositionUtil;
import com.galou.go4lunch.util.RatingUtil;
import com.google.android.gms.maps.model.LatLng;

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
    private LatLng location;

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
        return streamFetchRestaurantsNearBy(PositionUtil.convertLocationForApi(this.location))
                .map(ApiNearByResponse::getResults)
                .concatMap((Function<List<ResultApiPlace>, Observable<List<ApiDetailResponse>>>) results -> {
                    if(results != null && results.size() > 0) {
                         return Observable.fromIterable(results)
                                .concatMap((Function<ResultApiPlace, Observable<ApiDetailResponse>>) result -> streamFetchRestaurantDetails(result.getPlaceId()))
                                .toList()
                                .toObservable();
                    } else {
                        return null;
                    }
                });
    }

    public Observable<DistanceApiResponse> getDistanceToPoint(String point){
        return googlePlaceService.getDistancePoints(PositionUtil.convertLocationForApi(this.location), point)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public String getPhotoRestaurant(String photoReference){
        return String.format("%splace/photo?maxwidth=400&photoreference=%s&key=%s",
                BuildConfig.ApiPlaceBase, photoReference, BuildConfig.ApiPlaceKey);
    }

    public List<Restaurant> getRestaurantsLoaded(){
        return restaurants;
    }

    public void updateRestaurants(List<Restaurant> restaurants){
        this.restaurants = restaurants;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setRestaurantSelected(String restaurantUid){
        this.restaurantSelected = restaurantUid;
    }

    public String getRestaurantSelected(){
        return restaurantSelected;
    }

    public Restaurant createRestaurant(ResultApiPlace result){
        String uid = result.getPlaceId();
        String name = result.getName();
        Double latitude = result.getGeometry().getLocation().getLat();
        Double longitude = result.getGeometry().getLocation().getLng();
        String photo = this.getPhotoRestaurant(result.getPhotos().get(0).getPhotoReference());
        String address = result.getVicinity();
        int openingHours = OpeningHoursUtil.getOpeningTime(result.getOpeningHours());
        int rating = RatingUtil.calculateRating(result.getRating());
        String webSite = result.getWebsite();
        String phoneNumber = result.getPhoneNumber();
        return new Restaurant(uid, name, latitude, longitude, address, openingHours, photo, rating, phoneNumber, webSite);


    }


}
