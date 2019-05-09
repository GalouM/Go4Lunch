package com.galou.go4lunch.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.ApiResponse;
import com.galou.go4lunch.models.Result;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.RetryAction;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by galou on 2019-05-08
 */
public class MapViewViewModel extends BaseViewModel {

    private MutableLiveData<List<Result>> restaurantsList = new MutableLiveData<>();


    private RestaurantRepository restaurantRepository;
    private Disposable disposable;

    public LiveData<List<Result>> getRestaurantsList(){
        return restaurantsList;
    }

    public MapViewViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void start(String location){
        Log.e("location", location);
        this.disposable = restaurantRepository.getRestaurantsNearBy(location, 1500).subscribeWith(getObserverRestaurants());
    }

    @Override
    public void retry(RetryAction retryAction) {

    }

    private DisposableObserver<ApiResponse> getObserverRestaurants(){
        return new DisposableObserver<ApiResponse>() {
            @Override
            public void onNext(ApiResponse response) {
                Log.e("api call", String.valueOf(response.getStatus()));
                restaurantsList.setValue(response.getResults());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
}
