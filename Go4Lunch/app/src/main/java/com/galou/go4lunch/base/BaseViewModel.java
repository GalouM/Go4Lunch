package com.galou.go4lunch.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.Event;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by galou on 2019-04-23
 */
public abstract class BaseViewModel extends ViewModel {

    protected final MutableLiveData<Event<Integer>> snackBarText = new MutableLiveData<>();
    protected final MutableLiveData<Event<RetryAction>> snackBarWithAction = new MutableLiveData<>();

    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<Event<Integer>> getSnackBarMessage(){
        return snackBarText;
    }
    public LiveData<Event<RetryAction>> getSnackBarWithAction(){
        return snackBarWithAction;
    }

    protected User user;
    protected UserRepository userRepository;
    public abstract void retry(RetryAction retryAction);

    // --------------------
    // UTILS
    // --------------------

    protected String getCurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // --------------------
    // ERROR HANDLER
    // --------------------
    public OnFailureListener onFailureListener(RetryAction retryAction){
        return e -> {
            isLoading.setValue(false);
            snackBarWithAction.setValue(new Event<>(retryAction));
        };
    }


}
