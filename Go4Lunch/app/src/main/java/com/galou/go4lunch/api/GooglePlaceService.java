package com.galou.go4lunch.api;

import com.galou.go4lunch.models.ApiDetailResponse;
import com.galou.go4lunch.models.ApiNearByResponse;
import com.galou.go4lunch.models.DistanceApiResponse;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by galou on 2019-05-08
 */
public interface GooglePlaceService {

    final static String BASE_URL = "https://maps.googleapis.com/maps/api/";
    final static String API_KEY = "AIzaSyAsyZraxhl3hj1_bjYCPLVHbMgd6s62mxc";

    @GET("place/nearbysearch/json?type=restaurant&radius=1500&key=" + API_KEY)
    Observable<ApiNearByResponse> getRestaurantsNearBy(@Query("location") String location);

    @GET("place/details/json?fields=vicinity,name,place_id,id,geometry,opening_hours,international_phone_number,website,rating,utc_offset,photo&key=" + API_KEY)
    Observable<ApiDetailResponse> getRestaurantDetail(@Query("placeid") String placeId);

    @GET("distancematrix/json?&key=" + API_KEY)
    Observable<DistanceApiResponse> getDistancePoints(@Query("origins") String origins,
                                                      @Query("destinations") String destinations);



    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
