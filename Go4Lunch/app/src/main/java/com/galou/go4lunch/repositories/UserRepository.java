package com.galou.go4lunch.repositories;

import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Created by galou on 2019-05-03
 */
public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private CollectionReference userCollection;
    private User user;

    private static volatile UserRepository INSTANCE;

    public static UserRepository getInstance(){
        if(INSTANCE == null){
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
    }

    private UserRepository(){
        this.userCollection = getUserCollection();

    }

    private CollectionReference getUserCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //---- CREATE ----
    public Task<Void> createUser(String uid, String username, String email, String urlPicture){
        user = new User(uid, username, email, urlPicture);
        return userCollection.document(uid).set(user);
    }

    //---- GET ----
    public User getUser(){
        return user;
    }

    public Task<DocumentSnapshot> getUserFromFirebase(String uid){
        return userCollection.document(uid).get();

    }

    public Task<QuerySnapshot> getAllUsersFromFirebase(){
        return userCollection.orderBy("username").get();
    }

    //---- UPDATE ----
    public Task<Void> updateUserNameAndEmail(String username, String email, String uid){
        user.setUsername(username);
        user.setEmail(email);
        return userCollection.document(uid).update(
                "username", username, "email", email);
    }

    public Task<Void> updateRestaurantPicked(String restaurantUid, String restaurantName, String restaurantAddress, String uid){
        user.setRestaurantUid(restaurantUid);
        user.setRestaurantName(restaurantName);
        user.setRestaurantAddress(restaurantAddress);
        return userCollection.document(uid).update(
                "restaurantUid", restaurantUid,
                "restaurantName", restaurantName,
                "restaurantAddress", restaurantAddress);
    }

    public Task<Void> updateUrlPicture(String urlPicture, String uid){
        user.setUrlPicture(urlPicture);
        return userCollection.document(uid).update("urlPicture", urlPicture);
    }

    public Task<Void> addLikedRestaurant(String likedRestaurant, String uid){
        user.addLikedRestaurant(likedRestaurant);
        return updateLikedRestaurants(uid);
    }

    public Task<Void> removeLikedRestaurant(String likedRestaurant, String uid){
        user.removeLikedRestaurant(likedRestaurant);
        return updateLikedRestaurants(uid);
    }

    private Task<Void> updateLikedRestaurants(String uid){
        List<String> likedRestaurantsList = user.getLikedRestaurants();
        return userCollection.document(uid).update("likedRestaurants", likedRestaurantsList);
    }


    //---- DELETE ----
    public Task<Void> deleteUser(String uid){
        return userCollection.document(uid).delete();
    }

    public void updateUserRepository(User user){
        this.user = user;
    }

}
