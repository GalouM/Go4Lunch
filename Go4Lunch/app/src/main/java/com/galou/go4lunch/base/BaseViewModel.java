package com.galou.go4lunch.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.galou.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by galou on 2019-04-23
 */
public abstract class BaseViewModel extends ViewModel {

    protected final MutableLiveData<Integer> snackBarText = new MutableLiveData<>();

    public LiveData<Integer> getSnackBarMessage(){
        return snackBarText;
    }

    // --------------------
    // UTILS
    // --------------------

    protected String getCurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    // --------------------
    // ERROR HANDLER
    // --------------------
    public OnFailureListener onFailureListener(){
        return e -> snackBarText.setValue(R.string.error_unknown_error);
    }




}
