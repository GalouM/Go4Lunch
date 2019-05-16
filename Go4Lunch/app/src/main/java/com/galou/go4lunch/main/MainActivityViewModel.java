package com.galou.go4lunch.main;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.galou.go4lunch.BuildConfig;
import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.RetryAction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.galou.go4lunch.util.RetryAction.FETCH_USER;

/**
 * Created by galou on 2019-04-23
 */
public class MainActivityViewModel extends BaseViewModel {

    //----- PRIVATE LIVE DATA -----
    private final MutableLiveData<Object> logoutRequested = new MutableLiveData<>();
    private final MutableLiveData<Object> settingsRequested = new MutableLiveData<>();
    private final MutableLiveData<Object> openSignInActivityEvent = new MutableLiveData<>();
    private final MutableLiveData<Object> openDetailRestaurant = new MutableLiveData<>();

    //----- PUBLIC LIVE DATA -----
    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();

    // FOR TESTING
    @VisibleForTesting
    protected CountingIdlingResource espressoTestIdlingResource;

    private RestaurantRepository restaurantRepository;

    //----- GETTER LIVE DATA -----
    public LiveData<Object> getLogout() {
        return logoutRequested;
    }
    public LiveData<Object> getSettings() { return settingsRequested; }
    public LiveData<Object> getOpenDetailRestaurant() { return openDetailRestaurant; }

    public MainActivityViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.user = userRepository.getUser();
    }

    // --------------------
    // START
    // --------------------

    public void configureInfoUser(){
        username.setValue(user.getUsername());
        email.setValue(user.getEmail());
        urlPicture.setValue(user.getUrlPicture());
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
        settingsRequested.setValue(new Object());
    }

    public void updateRestaurantToDisplay() {
        String uidRestaurant = user.getRestaurant();
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
