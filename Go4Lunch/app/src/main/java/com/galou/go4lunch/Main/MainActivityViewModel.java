package com.galou.go4lunch.Main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by galou on 2019-04-23
 */
public class MainActivityViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> logoutRequested = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> loggedUser = new MutableLiveData<>();

    // LiveData getters

    public LiveData<Boolean> getLogout() {
        return logoutRequested;
    }
    public LiveData<FirebaseUser> getLoggedUser(){
        return loggedUser;
    }

    void logoutUserFromApp(){
        logoutRequested.setValue(true);
        snackBarText.setValue(R.string.logged_out_success);

    }

    void setLoggedUser(){
        loggedUser.setValue(getCurrentUser());
    }
}
