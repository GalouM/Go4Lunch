package com.galou.go4lunch.api;

import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by galou on 2019-04-23
 */
public class UserHelper {

    public static final String COLLECTION_NAME = "users";

    //---- COLLECTION REFERENCE ----
    public static CollectionReference getUserCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //---- CREATE ----
    public static Task<Void> createUser(String uid, String username, String urlPicture){
        User userToCreate = new User(uid, username, urlPicture);
        return UserHelper.getUserCollection().document(uid).set(userToCreate);
    }

    //---- GET ----
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUserCollection().document(uid).get();
    }

    //---- UPDATE ----
    public static Task<Void> updateUserName(String username, String uid){
        return UserHelper.getUserCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateRestaurant(Restaurant restaurant, String uid){
        return UserHelper.getUserCollection().document(uid).update("restaurant", restaurant);
    }

    public static Task<Void> updateUrlPicture(String urlPicture, String uid){
        return UserHelper.getUserCollection().document(uid).update("urlPicture", urlPicture);
    }

    //---- DELETE ----
    public static Task<Void> deleteUser(String uid){
        return UserHelper.getUserCollection().document(uid).delete();
    }

}
