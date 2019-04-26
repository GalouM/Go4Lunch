package com.galou.go4lunch.settings;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.galou.go4lunch.settings.SuccessOrign.UPDATE_USER;

/**
 * Created by galou on 2019-04-25
 */
public class SettingsViewModel extends BaseViewModel {

    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isNotificationEnabled = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private User user;

    void onStart(){
        isLoading.setValue(true);
        UserHelper.getUser(getCurrentUserUid())
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    onUserLogged();
                    isLoading.setValue(false);
                });

    }

    private void onUserLogged(){
        if (isUserLogged()) {
            username.setValue(user.getUsername());
            email.setValue(user.getEmail());
            urlPicture.setValue(user.getUrlPicture());
        }
    }

    void notificationStateChanged(boolean enabled){
        if(enabled){
            enableNotification();
            isNotificationEnabled.setValue(true);
        } else {
            disableNotification();
            isNotificationEnabled.setValue(false);
        }

    }

    void updateUserInfo(){
        isLoading.setValue(true);
        String newUsername = username.getValue();
        String newEmail = email.getValue();
        UserHelper.updateUserName(newUsername, getCurrentUserUid())
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(this.updateUserSuccessful(UPDATE_USER));
        UserHelper.updateEmail(newEmail, getCurrentUserUid()).addOnFailureListener(this.onFailureListener())
        .addOnSuccessListener(this.updateUserSuccessful(UPDATE_USER));

    }

    private void disableNotification(){
        snackBarText.setValue(R.string.notification_disabled);

    }

    private void enableNotification(){
        snackBarText.setValue(R.string.notifications_enabled);

    }

    private OnSuccessListener<Void> updateUserSuccessful(final SuccessOrign origin){
        return aVoid -> {
            switch (origin){
                case UPDATE_USER:
                    isLoading.setValue(false);
                    snackBarText.setValue(R.string.information_updated);
                    break;
            }

        };

    }

    private boolean isUserLogged(){
        return (user != null);
    }
}
