package com.galou.go4lunch.authentication;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by galou on 2019-04-22
 */
public class AuthenticationViewModel extends BaseViewModel {

    private final MutableLiveData<Object> openNewActivityEvent = new MutableLiveData<>();
    private final MutableLiveData<Object> openSignInActivityEvent = new MutableLiveData<>();

    // LiveData getters

    public LiveData<Object> getOpenNewActivityEvent() {
        return openNewActivityEvent;
    }

    public LiveData<Object> getOpenSignInActivityEvent() {
        return openSignInActivityEvent;
    }


    void handleResponseAfterSignIn(int requestCode, int resultCode, IdpResponse response){
        if (requestCode == AuthenticationActivity.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                openNewActivityEvent.setValue(new Object());
                createUserInFirestore();
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
            openNewActivityEvent.setValue(new Object());
        } else {
            openSignInActivityEvent.setValue(new Object());
        }
    }

    private void createUserInFirestore(){
        if(isCurrentUserLogged()){
            String urlPicture = (getCurrentUser().getPhotoUrl() != null) ?
                    this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = getCurrentUser().getDisplayName();
            String uid = getCurrentUser().getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }

    }


}
