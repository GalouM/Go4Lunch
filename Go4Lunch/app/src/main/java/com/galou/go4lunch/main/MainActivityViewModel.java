package com.galou.go4lunch.main;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.galou.go4lunch.BuildConfig;
import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.SaveDataRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.Event;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by galou on 2019-04-23
 */
public class MainActivityViewModel extends BaseViewModel {

    //----- PRIVATE LIVE DATA -----
    private final MutableLiveData<Event<Object>> logoutRequested = new MutableLiveData<>();
    private final MutableLiveData<Event<Object>> settingsRequested = new MutableLiveData<>();
    private final MutableLiveData<Event<Object>> openDetailRestaurant = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isNotificationEnable = new MutableLiveData<>();
    private final MutableLiveData<LatLng> location = new MutableLiveData<>();

    //----- PUBLIC LIVE DATA -----
    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();

    // FOR TESTING
    @VisibleForTesting
    protected CountingIdlingResource espressoTestIdlingResource;

    private RestaurantRepository restaurantRepository;
    private SaveDataRepository saveDataRepository;

    //----- GETTER LIVE DATA -----
    public LiveData<Event<Object>> getLogout() {
        return logoutRequested;
    }
    public LiveData<Event<Object>> getSettings() { return settingsRequested; }
    public LiveData<Event<Object>> getOpenDetailRestaurant() { return openDetailRestaurant; }
    public LiveData<Boolean> getIsNotificationEnable(){ return isNotificationEnable; }
    public LiveData<LatLng> getLocation(){ return location; }

    public MainActivityViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository, SaveDataRepository saveDataRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.saveDataRepository = saveDataRepository;
        this.user = userRepository.getUser();
    }

    // --------------------
    // START
    // --------------------

    public void configureSaveDataRepo(Context context){
        if(saveDataRepository.getPreferences() == null){
            saveDataRepository.configureContext(context);
        }
    }

    public void configureInfoUser(){
        username.setValue(user.getUsername());
        email.setValue(user.getEmail());
        urlPicture.setValue(user.getUrlPicture());
        saveDataRepository.saveUserId(user.getUid());
        isNotificationEnable.setValue(saveDataRepository.getNotificationSettings(user.getUid()));
    }

    // --------------------
    // GET USER ACTION
    // --------------------

    public void logoutUserFromApp(){
        this.configureEspressoIdlingResource();
        this.incrementIdleResource();
        logoutRequested.setValue(new Event<>(new Object()));
        snackBarText.setValue(new Event<>(R.string.logged_out_success));

    }

    public void openSettings(){
        settingsRequested.setValue(new Event<>(new Object()));
    }

    public void showUserRestaurant() {
        String uidRestaurant = user.getRestaurantUid();
        if(uidRestaurant != null) {
            restaurantRepository.setRestaurantSelected(uidRestaurant);
            openDetailRestaurant.setValue(new Event<>(new Object()));
        } else {
            snackBarText.setValue(new Event<>(R.string.no_restaurant_picked_message));
        }
    }

    public void configureLocationUser(LatLng location){
        restaurantRepository.setLocation(location);
        this.location.setValue(location);

    }

    public void noLocationAvailable(){
        snackBarText.setValue(new Event<>(R.string.no_location_message));

    }

    public void showRestaurantSelected(String uid){
        restaurantRepository.setRestaurantSelected(uid);
        openDetailRestaurant.setValue(new Event<>(new Object()));
    }

    @Override
    public void retry(RetryAction retryAction) {

    }

    // -----------------
    // FOR TESTING
    // -----------------

    @VisibleForTesting
    public CountingIdlingResource getEspressoIdlingResource() { return espressoTestIdlingResource; }

    @VisibleForTesting
    private void configureEspressoIdlingResource(){
        this.espressoTestIdlingResource = new CountingIdlingResource("Network_Call");
    }

    void incrementIdleResource(){
        if (BuildConfig.DEBUG) this.espressoTestIdlingResource.increment();
    }

    void decrementIdleResource(){
        if (BuildConfig.DEBUG) this.espressoTestIdlingResource.decrement();
    }

}
