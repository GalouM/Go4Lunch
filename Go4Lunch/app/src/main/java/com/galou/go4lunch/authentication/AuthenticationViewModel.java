package com.galou.go4lunch.authentication;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.RetryAction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.galou.go4lunch.util.RetryAction.FETCH_USER;

/**
 * Created by galou on 2019-04-22
 */
public class AuthenticationViewModel extends BaseViewModel {

    private final MutableLiveData<Object> openNewActivityEvent = new MutableLiveData<>();
    private final MutableLiveData<Object> openSignInActivityEvent = new MutableLiveData<>();

    private User user;

    // LiveData getters

    public LiveData<Object> getOpenNewActivityEvent() {
        return openNewActivityEvent;
    }

    public LiveData<Object> getOpenSignInActivityEvent() {
        return openSignInActivityEvent;
    }

    public AuthenticationViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    void handleResponseAfterSignIn(int requestCode, int resultCode, IdpResponse response){
        if (requestCode == AuthenticationActivity.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.fetchCurrentUserFromFirestore();
            } else { // ERRORS
                if (response == null && response.getError() != null) {
                    snackBarText.setValue(R.string.error_authentication_canceled);
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    snackBarWithAction.setValue(FETCH_USER);
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    snackBarWithAction.setValue(FETCH_USER);
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
            userRepository.getUserFromFirebase(getCurrentUser().getUid())
                    .addOnFailureListener(this.onFailureListener(FETCH_USER))
                    .addOnSuccessListener(documentSnapshot -> {
                        user = documentSnapshot.toObject(User.class);
                        if (user == null){
                            createUserInFirestore();
                        } else {
                            userRepository.updateUserRepository(user);
                            openNewActivityEvent.setValue(new Object());
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
        userRepository.createUser(uid, username, email, urlPicture)
                .addOnFailureListener(this.onFailureListener(FETCH_USER))
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

    @Override
    public void retry(RetryAction retryAction) {
        if(retryAction == FETCH_USER){
            fetchCurrentUserFromFirestore();
        }

    }

}
