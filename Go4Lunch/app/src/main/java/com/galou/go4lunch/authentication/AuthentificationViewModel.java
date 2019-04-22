package com.galou.go4lunch.authentication;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.galou.go4lunch.Main.MainActivity;
import com.galou.go4lunch.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by galou on 2019-04-22
 */
public class AuthentificationViewModel extends ViewModel {

    private final MutableLiveData<Integer> snackBarText = new MutableLiveData<>();
    private final MutableLiveData<Object> openNewActivityEvent = new MutableLiveData<>();

    // LiveData getters
    public LiveData<Integer> getSnackBarMessage(){
        return snackBarText;
    }

    public MutableLiveData<Object> getOpenNewActivityEvent() {
        return openNewActivityEvent;
    }

    void handleResponseAfterSignIn(int requestCode, int resultCode, IdpResponse response){
        if (requestCode == AuthenticationActivity.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                openNewActivityEvent.setValue(new Object());
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

}
