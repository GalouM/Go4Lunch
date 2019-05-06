package com.galou.go4lunch.main;

import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.galou.go4lunch.BuildConfig;
import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.injection.UserRepository;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.RetryAction;

import static com.galou.go4lunch.util.RetryAction.FETCH_USER;

/**
 * Created by galou on 2019-04-23
 */
public class MainActivityViewModel extends BaseViewModel {

    //----- PRIVATE LIVE DATA -----
    private final MutableLiveData<Object> logoutRequested = new MutableLiveData<>();
    private final MutableLiveData<Object> settingsRequested = new MutableLiveData<>();

    //----- PUBLIC LIVE DATA -----
    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();

    // FOR TESTING
    @VisibleForTesting
    protected CountingIdlingResource espressoTestIdlingResource;

    private User user;

    //----- GETTER LIVE DATA -----
    public LiveData<Object> getLogout() {
        return logoutRequested;
    }
    public LiveData<Object> getSettings() { return settingsRequested; }

    public MainActivityViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --------------------
    // START
    // --------------------

    public void configureUser(){
        if(userRepository.getUser() == null) {
            userRepository.getUserFromFirebase(getCurrentUserUid())
                    .addOnFailureListener(this.onFailureListener(FETCH_USER))
                    .addOnSuccessListener(documentSnapshot -> {
                        user = documentSnapshot.toObject(User.class);
                        this.configureInfoUser();
                        userRepository.updateUserRepository(user);
                    });
        } else {
            user = userRepository.getUser();
            this.configureInfoUser();
        }
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

    // --------------------
    // UPDATE BINDING INFOS
    // --------------------

    private void configureInfoUser(){
        username.setValue(user.getUsername());
        email.setValue(user.getEmail());
        urlPicture.setValue(user.getUrlPicture());
    }

    @Override
    public void retry(RetryAction retryAction) {
        if (retryAction == FETCH_USER){
            this.configureUser();
        }

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
