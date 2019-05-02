package com.galou.go4lunch.settings;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.UUID;

import static com.galou.go4lunch.settings.SuccessOrign.DELETE_USER;
import static com.galou.go4lunch.settings.SuccessOrign.UPDATE_PHOTO;
import static com.galou.go4lunch.settings.SuccessOrign.UPDATE_USER;
import static com.galou.go4lunch.util.RetryAction.DELETE_USER_DB;
import static com.galou.go4lunch.util.RetryAction.FETCH_USER;
import static com.galou.go4lunch.util.RetryAction.UPDATE_INFO_USER_DB;
import static com.galou.go4lunch.util.RetryAction.UPDATE_PHOTO_DB;
import static com.galou.go4lunch.util.TextUtil.isEmailCorrect;
import static com.galou.go4lunch.util.TextUtil.isTextLongEnough;
import static com.galou.go4lunch.util.UserConverter.convertJsonInUser;
import static com.galou.go4lunch.util.UserConverter.convertUserInJson;

/**
 * Created by galou on 2019-04-25
 */
public class SettingsViewModel extends BaseViewModel {

    //----- PUBLIC LIVE DATA -----
    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> urlPicture = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isNotificationEnabled = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isEmailError = new MutableLiveData<>();
    public final MutableLiveData<Integer> errorMessageEmail = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isUsernameError = new MutableLiveData<>();
    public final MutableLiveData<Integer> errorMessageUsername = new MutableLiveData<>();

    //----- PRIVATE LIVE DATA -----
    private final MutableLiveData<Object> deleteUser = new MutableLiveData<>();
    private final MutableLiveData<Object> openDialog = new MutableLiveData<>();
    private final MutableLiveData<String> modifiedUser = new MutableLiveData<>();

    //----- GETTER -----
    public LiveData<Object> getDeleteUser(){
        return deleteUser;
    }
    public LiveData<Object> getOpenDialog() {
        return openDialog;
    }
    public LiveData<String> getModifiedUser() { return modifiedUser; }

    private String newUsername;
    private String newEmail;
    private String newPhotoUrl;
    private String urlPhotoSelected;

    // --------------------
    // START
    // --------------------

    public void configureUser(String jsonUser){
        isLoading.setValue(true);
        if(jsonUser != null){
            this.user = convertJsonInUser(jsonUser);
            configureInfoUser();
        }
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

    public void notificationStateChanged(boolean enabled){
        if(enabled){
            enableNotification();
            isNotificationEnabled.setValue(true);
        } else {
            disableNotification();
            isNotificationEnabled.setValue(false);
        }

    }

    public void updateUserInfo(){
        isLoading.setValue(true);
        isEmailError.setValue(false);
        isUsernameError.setValue(false);
        newUsername = username.getValue();
        newEmail = email.getValue();
        if(isNewUserInfosCorrect(newEmail, newUsername)){
            UserHelper.updateUserNameAndEmail(newUsername, newEmail, getCurrentUserUid())
                    .addOnFailureListener(this.onFailureListener(UPDATE_INFO_USER_DB))
                    .addOnSuccessListener(this.onSuccessListener(UPDATE_USER));
        } else {
            isLoading.setValue(false);
        }

    }

    public void deleteUserFromDBRequest() {
        openDialog.setValue(new Object());

    }

    public void deleteUserFromDB() {
        isLoading.setValue(true);
        UserHelper.deleteUser(getCurrentUserUid())
                .addOnSuccessListener(this.onSuccessListener(DELETE_USER))
                .addOnFailureListener(this.onFailureListener(DELETE_USER_DB));
    }

    public void updateUserPhoto(String urlPhoto) {
        isLoading.setValue(true);
        urlPhotoSelected = urlPhoto;
        uploadPhotoInFirebase(urlPhoto);
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
                .addOnFailureListener(this.onFailureListener(UPDATE_PHOTO_DB));

    }

    private void getUrlPhotoFromFirebase(UploadTask.TaskSnapshot taskSnapshot){
        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    newPhotoUrl = uri.toString();
                    UserHelper.updateUrlPicture(newPhotoUrl, getCurrentUserUid())
                            .addOnSuccessListener(onSuccessListener(UPDATE_PHOTO))
                            .addOnFailureListener(this.onFailureListener(UPDATE_PHOTO_DB));
                }).addOnFailureListener(this.onFailureListener(UPDATE_PHOTO_DB));

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
                    user.setEmail(newEmail);
                    user.setUsername(newUsername);
                    configureModifiedUser();
                    break;
                case DELETE_USER:
                    snackBarText.setValue(R.string.deleted_account_message);
                    deleteUser.setValue(new Object());
                    isLoading.setValue(false);
                    break;
                case UPDATE_PHOTO:
                    snackBarText.setValue(R.string.photo_updated_message);
                    isLoading.setValue(false);
                    urlPicture.setValue(newPhotoUrl);
                    user.setUrlPicture(newPhotoUrl);
                    configureModifiedUser();
                    break;
            }

        };

    }

    private void configureModifiedUser(){
        modifiedUser.setValue(convertUserInJson(user));
    }

    @Override
    public void retry(RetryAction retryAction) {
        switch (retryAction){
            case UPDATE_PHOTO_DB:
                updateUserPhoto(urlPhotoSelected);
                break;
            case UPDATE_INFO_USER_DB:
                updateUserInfo();
                break;
            case DELETE_USER_DB:
                deleteUserFromDB();
                break;

        }

    }

}
