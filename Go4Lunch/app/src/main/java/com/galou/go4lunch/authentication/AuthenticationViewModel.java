package com.galou.go4lunch.authentication;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import static android.app.Activity.RESULT_OK;

/**
 * Created by galou on 2019-04-22
 */
public class AuthenticationViewModel extends BaseViewModel {

    private final MutableLiveData<String> openNewActivityEvent = new MutableLiveData<>();
    private final MutableLiveData<Object> openSignInActivityEvent = new MutableLiveData<>();

    private User user;

    // LiveData getters

    public LiveData<String> getOpenNewActivityEvent() {
        return openNewActivityEvent;
    }

    public LiveData<Object> getOpenSignInActivityEvent() {
        return openSignInActivityEvent;
    }


    void handleResponseAfterSignIn(int requestCode, int resultCode, IdpResponse response){
        if (requestCode == AuthenticationActivity.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.fetchCurrentUserFromFirestore();
            } else { // ERRORS
                if (response == null) {
                    snackBarText.setValue(R.string.error_authentication_canceled);
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    snackBarText.setValue(R.string.error_no_internet);
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    snackBarText.setValue(R.string.error_unknown_error);
                }
            }
        }

    }

    void checkIfUserIsLogged(){
        if (isCurrentUserLogged()){
            this.fetchCurrentUserFromFirestore();
        } else {
            openSignInActivityEvent.setValue(new Object());
        }
    }

    private void fetchCurrentUserFromFirestore(){
        if (isCurrentUserLogged()) {
            UserHelper.getUser(getCurrentUser().getUid())
                    .addOnFailureListener(this.onFailureListener())
                    .addOnSuccessListener(documentSnapshot -> {
                        user = documentSnapshot.toObject(User.class);
                        if (user == null){
                            createUserInFirestore();
                        } else {
                            Gson gson = new Gson();
                            String jsonUser = gson.toJson(user);
                            openNewActivityEvent.setValue(jsonUser);
                        }
                    });


        }


    }

    private void createUserInFirestore() {
        String urlPicture = (getCurrentUser().getPhotoUrl() != null) ?
                this.getCurrentUser().getPhotoUrl().toString() : null;
        String email = getCurrentUser().getEmail();
        String username = getCurrentUser().getDisplayName();
        String uid = getCurrentUser().getUid();
        UserHelper.createUser(uid, username, email, urlPicture)
                .addOnFailureListener(this.onFailureListener())
                .addOnSuccessListener(aVoid -> fetchCurrentUserFromFirestore());


    }

    // --------------------
    // UTILS
    // --------------------

    @Nullable
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }


}
