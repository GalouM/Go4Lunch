package com.galou.go4lunch.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.UserRepository;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by galou on 2019-05-22
 */
public class EraseRestaurantInfo extends BroadcastReceiver {

    private UserRepository userRepository;

    @Override
    public void onReceive(Context context, Intent intent) {
        userRepository = UserRepository.getInstance();
        this.eraseRestaurantInfo();


    }

    private void eraseRestaurantInfo() {
        userRepository.getAllUsersFromFirebase()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        User user = documentSnapshot.toObject(User.class);
                        if(user != null && user.getRestaurantUid() != null) {
                            userRepository.updateRestaurantPicked(null, null, null, user.getUid());
                        }
                    }
                });
    }
}
