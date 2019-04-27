package com.galou.go4lunch.settings;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import static com.galou.go4lunch.settings.SuccessOrign.DELETE_USER;
import static com.galou.go4lunch.settings.SuccessOrign.UPDATE_PHOTO;
import static com.galou.go4lunch.settings.SuccessOrign.UPDATE_USER;

/**
 * Created by galou on 2019-04-25
 */
public class SettingsViewModel extends BaseViewModel {

    //----- PUBLIC LIVE DATA -----

    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isNotificationEnabled = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    //----- PRIVATE LIVE DATA -----
    private final MutableLiveData<Object> deleteUser = new MutableLiveData<>();

    //----- GETTER -----
    public LiveData<Object> getDeleteUser(){
        return deleteUser;
    }

    private User user;

    void onStart(){
        isLoading.setValue(true);
        UserHelper.getUser(getCurrentUserUid())
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    configureInfoUser();
                    isLoading.setValue(false);
                });

    }

    private void configureInfoUser(){
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
        UserHelper.updateUserNameAndEmail(newUsername, newEmail, getCurrentUserUid())
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(this.onSuccessListener(UPDATE_USER));

    }

    private void disableNotification(){
        snackBarText.setValue(R.string.notification_disabled);

    }

    private void enableNotification(){
        snackBarText.setValue(R.string.notifications_enabled);

    }

    private OnSuccessListener<Void> onSuccessListener(final SuccessOrign origin){
        return aVoid -> {
            switch (origin){
                case UPDATE_USER:
                    isLoading.setValue(false);
                    snackBarText.setValue(R.string.information_updated);
                    break;
                case DELETE_USER:
                    isLoading.setValue(false);
                    snackBarText.setValue(R.string.deleted_account_message);
                    deleteUser.setValue(new Object());
                    break;
                case UPDATE_PHOTO:
                    isLoading.setValue(false);
                    snackBarText.setValue(R.string.photo_updated_message);
                    break;
            }

        };

    }

    private boolean isUserLogged(){
        return (user != null);
    }

    void deleteUserFromDB() {
        isLoading.setValue(true);
        UserHelper.deleteUser(getCurrentUserUid())
                .addOnSuccessListener(this.onSuccessListener(DELETE_USER))
                .addOnFailureListener(this.onFailureListener());
    }

    void updateUserPhoto(String urlPhoto) {
        isLoading.setValue(true);
        uploadPhotoInFirebase(urlPhoto);
    }

    private void uploadPhotoInFirebase(final String urlPhoto) {
        String uuid = UUID.randomUUID().toString();
        StorageReference imageRef = FirebaseStorage.getInstance().getReference(uuid);
        imageRef.putFile(Uri.parse(urlPhoto)).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String pathImageInFirebase = uri.toString();
                        UserHelper.updateUrlPicture(pathImageInFirebase, getCurrentUserUid())
                                .addOnSuccessListener(onSuccessListener(UPDATE_PHOTO))
                                .addOnFailureListener(onFailureListener());
                        urlPicture.setValue(urlPhoto);
                    }).addOnFailureListener(onFailureListener());

        }).addOnFailureListener(onFailureListener());

    }

    @Override
    public OnFailureListener onFailureListener() {
        isLoading.setValue(false);
        return super.onFailureListener();
    }
}