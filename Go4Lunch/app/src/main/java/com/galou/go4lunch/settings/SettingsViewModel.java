package com.galou.go4lunch.settings;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static com.galou.go4lunch.settings.SuccessOrign.DELETE_USER;
import static com.galou.go4lunch.settings.SuccessOrign.UPDATE_PHOTO;
import static com.galou.go4lunch.settings.SuccessOrign.UPDATE_USER;
import static com.galou.go4lunch.util.TextUtil.isEmailCorrect;
import static com.galou.go4lunch.util.TextUtil.isTextLongEnough;

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
    public final MutableLiveData<Boolean> isEmailError = new MutableLiveData<>();
    public final MutableLiveData<Integer> errorMessageEmail = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isUsernameError = new MutableLiveData<>();
    public final MutableLiveData<Integer> errorMessageUsername = new MutableLiveData<>();

    //----- PRIVATE LIVE DATA -----
    private final MutableLiveData<Object> deleteUser = new MutableLiveData<>();
    private final MutableLiveData<Object> openDialog = new MutableLiveData<>();

    //----- GETTER -----
    public LiveData<Object> getDeleteUser(){
        return deleteUser;
    }
    public LiveData<Object> getOpenDialog() {
        return openDialog;
    }

    private User user;

    // --------------------
    // START
    // --------------------

    void onStart(){
        isLoading.setValue(true);
        UserHelper.getUser(getCurrentUserUid())
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    configureInfoUser();
                });
    }

    // --------------------
    // UPDATE BINDING INFO
    // --------------------

    private void configureInfoUser(){
        if (user != null) {
            username.setValue(user.getUsername());
            email.setValue(user.getEmail());
            urlPicture.setValue(user.getUrlPicture());
            isLoading.setValue(false);
        }
    }

    // --------------------
    // GET USER ACTION
    // --------------------

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
        isEmailError.setValue(false);
        isUsernameError.setValue(false);
        String newUsername = username.getValue();
        String newEmail = email.getValue();
        if(isNewUserInfosCorrect(newEmail, newUsername)){
            UserHelper.updateUserNameAndEmail(newUsername, newEmail, getCurrentUserUid())
                    .addOnFailureListener(this.onFailureListener())
                    .addOnSuccessListener(this.onSuccessListener(UPDATE_USER));
        } else {
            isLoading.setValue(false);
        }

    }

    void deleteUserFromDBRequest() {
        openDialog.setValue(new Object());

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
        urlPicture.setValue(urlPhoto);
    }

    // --------------------
    // SET NOTIFICATION
    // --------------------

    private void disableNotification(){
        snackBarText.setValue(R.string.notification_disabled);

    }

    private void enableNotification(){
        snackBarText.setValue(R.string.notifications_enabled);

    }

    // --------------------
    // SET NEW PICTURE
    // --------------------

    private void uploadPhotoInFirebase(final String urlPhoto) {
        String uuid = UUID.randomUUID().toString();
        StorageReference imageRef = FirebaseStorage.getInstance().getReference(uuid);
        imageRef.putFile(Uri.parse(urlPhoto))
                .addOnSuccessListener(this::getUrlPhotoFromFirebase)
                .addOnFailureListener(onFailureListener());

    }

    private void getUrlPhotoFromFirebase(UploadTask.TaskSnapshot taskSnapshot){
        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    String pathImageInFirebase = uri.toString();
                    UserHelper.updateUrlPicture(pathImageInFirebase, getCurrentUserUid())
                            .addOnSuccessListener(onSuccessListener(UPDATE_PHOTO))
                            .addOnFailureListener(onFailureListener());
                }).addOnFailureListener(onFailureListener());

    }

    // --------------------
    // UTILS
    // --------------------

    private boolean isNewUserInfosCorrect(String email, String username){
        if(!isEmailCorrect(email)){
            isEmailError.setValue(true);
            errorMessageEmail.setValue(R.string.incorrect_email);
            return false;
        }
        if(!isTextLongEnough(username, 3)){
            isUsernameError.setValue(true);
            errorMessageUsername.setValue(R.string.incorrect_username);
            return false;
        }

        return true;

    }

    private OnSuccessListener<Void> onSuccessListener(final SuccessOrign origin){
        return aVoid -> {
            switch (origin){
                case UPDATE_USER:
                    snackBarText.setValue(R.string.information_updated);
                    isLoading.setValue(false);
                    break;
                case DELETE_USER:
                    snackBarText.setValue(R.string.deleted_account_message);
                    deleteUser.setValue(new Object());
                    isLoading.setValue(false);
                    break;
                case UPDATE_PHOTO:
                    snackBarText.setValue(R.string.photo_updated_message);
                    isLoading.setValue(false);
                    break;
            }

        };

    }

}
