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
import com.galou.go4lunch.util.RetryAction;

/**
 * Created by galou on 2019-04-23
 */
public class MainActivityViewModel extends BaseViewModel {

    //----- PRIVATE LIVE DATA -----
    private final MutableLiveData<Object> logoutRequested = new MutableLiveData<>();
    private final MutableLiveData<Boolean> settingsRequested = new MutableLiveData<>();
    private final MutableLiveData<Object> openDetailRestaurant = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isNotificationEnable = new MutableLiveData<>();

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
    public LiveData<Object> getLogout() {
        return logoutRequested;
    }
    public LiveData<Boolean> getSettings() { return settingsRequested; }
    public LiveData<Object> getOpenDetailRestaurant() { return openDetailRestaurant; }
    public LiveData<Boolean> getIsNotificationEnable(){ return isNotificationEnable; }

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
        logoutRequested.setValue(new Object());
        snackBarText.setValue(R.string.logged_out_success);

    }

    public void openSettings(){
        settingsRequested.setValue(true);
    }
    public void closeSettings() { settingsRequested.setValue(false);}

    public void updateRestaurantToDisplay() {
        String uidRestaurant = user.getRestaurantUid();
        if(uidRestaurant != null) {
            restaurantRepository.setRestaurantSelected(uidRestaurant);
            openDetailRestaurant.setValue(new Object());
        } else {
            snackBarText.setValue(R.string.no_restaurant_picked_message);
        }
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
