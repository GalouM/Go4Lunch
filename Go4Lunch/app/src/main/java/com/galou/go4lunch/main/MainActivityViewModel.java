package com.galou.go4lunch.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by galou on 2019-04-23
 */
public class MainActivityViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> logoutRequested = new MutableLiveData<>();
    private final MutableLiveData<Boolean> settingsRequested = new MutableLiveData<>();
    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();

    private User user;

    // LiveData getters

    public LiveData<Boolean> getLogout() {
        return logoutRequested;
    }
    public LiveData<Boolean> getSettings() { return settingsRequested; }

    void onStart(){
        UserHelper.getUser(getCurrentUserUid())
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    this.onUserLogged();
                });
    }

    void logoutUserFromApp(){
        logoutRequested.setValue(true);
        snackBarText.setValue(R.string.logged_out_success);

    }


    private void onUserLogged(){
        if (isUserLogged()) {
            username.setValue(user.getUsername());
            email.setValue(user.getEmail());
            urlPicture.setValue(user.getUrlPicture());
        }
    }

    void openSettings(){
        settingsRequested.setValue(true);
    }

    private boolean isUserLogged(){
        return (user != null);
    }



}
