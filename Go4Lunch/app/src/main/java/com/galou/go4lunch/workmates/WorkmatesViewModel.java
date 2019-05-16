package com.galou.go4lunch.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.RetryAction;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galou on 2019-04-27
 */
public class WorkmatesViewModel extends BaseViewModel {
    
    private RestaurantRepository restaurantRepository;

    //----- PRIVATE LIVE DATA -----
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<Object> openDetailRestaurant = new MutableLiveData<>();

    //----- GETTER LIVE DATA -----
    public LiveData<List<User>> getUsers(){ return users; }

    public LiveData<Object> getOpenDetailRestaurant() {
        return openDetailRestaurant;
    }

    public WorkmatesViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.user = userRepository.getUser();
    }

    // --------------------
    // GET USER ACTION
    // --------------------

    void fetchListUsersFromFirebase() {
        userRepository.getAllUsersFromFirebase()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> fetchedUsers = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        User userFetched = documentSnapshot.toObject(User.class);
                        if(!userFetched.getUid().equals(user.getUid())) {
                            fetchedUsers.add(userFetched);
                        }
                    }
                    users.setValue(fetchedUsers);
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

    public void updateRestaurantToDisplay(User userSelected) {
        String uidRestaurant = userSelected.getRestaurant();
        if(uidRestaurant != null) {
            restaurantRepository.setRestaurantSelected(uidRestaurant);
            openDetailRestaurant.setValue(new Object());
        }
    }
}
