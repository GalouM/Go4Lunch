package com.galou.go4lunch.main;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();

    // LiveData getters

    public LiveData<Boolean> getLogout() {
        return logoutRequested;
    }

    void logoutUserFromApp(){
        logoutRequested.setValue(true);
        snackBarText.setValue(R.string.logged_out_success);

    }

    void onUserLogged(){
        if (getCurrentUser()!= null) {
            username.setValue(getCurrentUser().getDisplayName());
            email.setValue(getCurrentUser().getEmail());
            urlPicture.setValue((getCurrentUser().getPhotoUrl() != null) ?
                    getCurrentUser().getPhotoUrl().toString() : null);
        }
    }

}
