package com.galou.go4lunch.base;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by galou on 2019-04-23
 */
public abstract class BaseViewModel extends ViewModel {

    protected final MutableLiveData<Integer> snackBarText = new MutableLiveData<>();
    protected final MutableLiveData<RetryAction> snackBarWithAction = new MutableLiveData<>();

    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<Integer> getSnackBarMessage(){
        return snackBarText;
    }
    public LiveData<RetryAction> getSnackBarWithAction(){
        return snackBarWithAction;
    }

    protected User user;
    public abstract void retry(RetryAction retryAction);

    // --------------------
    // UTILS
    // --------------------

    protected String getCurrentUserUid(){
        return user.getUid();
    }

    // --------------------
    // ERROR HANDLER
    // --------------------
    public OnFailureListener onFailureListener(RetryAction retryAction){
        return e -> {
            Log.e("tag", "failed");
            isLoading.setValue(false);
            snackBarWithAction.setValue(retryAction);
        };
    }


}
