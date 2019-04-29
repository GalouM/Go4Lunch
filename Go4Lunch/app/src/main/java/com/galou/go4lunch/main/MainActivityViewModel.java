package com.galou.go4lunch.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;

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

    private User user;

    //----- GETTER LIVE DATA -----
    public LiveData<Object> getLogout() {
        return logoutRequested;
    }
    public LiveData<Object> getSettings() { return settingsRequested; }

    // --------------------
    // START
    // --------------------

    void onStart(){
        UserHelper.getUser(getCurrentUserUid())
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    this.configureInfoUser();
                });
    }

    // --------------------
    // GET USER ACTION
    // --------------------

    void logoutUserFromApp(){
        logoutRequested.setValue(new Object());
        snackBarText.setValue(R.string.logged_out_success);

    }

    void openSettings(){
        settingsRequested.setValue(new Object());
    }

    // --------------------
    // UPDATE BINDING INFOS
    // --------------------

    private void configureInfoUser(){
        if (user != null) {
            username.setValue(user.getUsername());
            email.setValue(user.getEmail());
            urlPicture.setValue(user.getUrlPicture());


        } else {
            onStart();
        }
    }

}
