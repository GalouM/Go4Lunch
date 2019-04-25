package com.galou.go4lunch.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;

/**
 * Created by galou on 2019-04-25
 */
public class SettingsViewModel extends BaseViewModel {

    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isNotificationEnabled = new MutableLiveData<>();

    void onStart(){
        if (getCurrentUser()!= null) {
            username.setValue(getCurrentUser().getDisplayName());
            email.setValue(getCurrentUser().getEmail());
            urlPicture.setValue((getCurrentUser().getPhotoUrl() != null) ?
                    getCurrentUser().getPhotoUrl().toString() : null);
        }
    }

    void notificationStateChanged(boolean enabled){
        if(enabled){
            enableNotification();
        } else {
            disableNotification();
        }

    }

    private void disableNotification(){
        snackBarText.setValue(R.string.notification_disabled);

    }

    private void enableNotification(){
        snackBarText.setValue(R.string.notifications_enabled);

    }
}
