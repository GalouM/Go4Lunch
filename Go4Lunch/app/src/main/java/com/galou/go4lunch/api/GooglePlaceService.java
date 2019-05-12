package com.galou.go4lunch.api;

import android.graphics.Bitmap;

import com.galou.go4lunch.models.ApiResponse;
import com.galou.go4lunch.models.DistanceApi;

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

    @GET("place/nearbysearch/json?type=restaurant&key=" + API_KEY)
    Observable<ApiResponse> getRestaurantsNearBy(@Query("location") String location,
                                                 @Query("radius") Integer radius);

    @GET("distancematrix/json?&key=" + API_KEY)
    Observable<DistanceApi> getDistancePoints(@Query("origins") String origins,
                                              @Query("destinations") String destinations);

    @GET("photo?maxwidth=400&key=" + API_KEY)
    Observable<String> getPhotoFromPlace(@Query("photoreference") String photoReference);


    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
