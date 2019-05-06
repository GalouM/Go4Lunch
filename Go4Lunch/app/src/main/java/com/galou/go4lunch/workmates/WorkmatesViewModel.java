package com.galou.go4lunch.workmates;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.injection.UserRepository;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.RetryAction;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galou on 2019-04-27
 */
public class WorkmatesViewModel extends BaseViewModel {

    //----- PRIVATE LIVE DATA -----
    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    //----- GETTER LIVE DATA -----
    public LiveData<List<User>> getUsers(){ return users; }

    public WorkmatesViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --------------------
    // GET USER ACTION
    // --------------------

    void fetchListUsersFromFirebase() {
        userRepository.getAllUsersFromFirebase()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> fetchedUser = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        User user = documentSnapshot.toObject(User.class);
                        if(!user.getUid().equals(getCurrentUserUid())) {
                            fetchedUser.add(user);
                        }
                    }
                    users.setValue(fetchedUser);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(this.onFailureListener(RetryAction.FETCH_ALL_USERS));

    }

    public void onRefreshUserList(){
        isLoading.setValue(true);
        this.fetchListUsersFromFirebase();

    }

    @Override
    public void retry(RetryAction retryAction) {
        if(retryAction == RetryAction.FETCH_ALL_USERS){
            fetchListUsersFromFirebase();
        }


    }
}
