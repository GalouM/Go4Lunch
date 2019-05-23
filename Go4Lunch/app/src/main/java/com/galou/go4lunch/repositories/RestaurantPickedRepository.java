package com.galou.go4lunch.repositories;

import com.galou.go4lunch.models.RestaurantPicked;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RestaurantPickedRepository {

    private static final String COLLECTION_NAME = "restaurantPicked";
    private CollectionReference restaurantPickedCollection;
    private RestaurantPicked restaurantPicked;

    private static volatile RestaurantPickedRepository INSTANCE;

    public static RestaurantPickedRepository getInstance(){
        if(INSTANCE == null){
            INSTANCE = new RestaurantPickedRepository();
        }
        return INSTANCE;
    }

    private RestaurantPickedRepository(){
        this.restaurantPickedCollection = getrestaurantPickedCollection();

    }

    private CollectionReference getrestaurantPickedCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //---- CREATE ----
    public Task<Void> createRestarant(String uid, String name, String phtoUrl, String address, int rating, String website, String phoneNumber, String datePicked){
        restaurantPicked = new RestaurantPicked(uid, name, phtoUrl, address, rating, website, phoneNumber, datePicked);
        return restaurantPickedCollection.document(uid).set(restaurantPicked);
    }

    //---- GET ----
    public RestaurantPicked getRestaurant(){
        return restaurantPicked;
    }

    public Task<DocumentSnapshot> getRestaurantFromFirebase(String uid){
        return restaurantPickedCollection.document(uid).get();

    }

    public Task<QuerySnapshot> getAllRestaurantsFromFirebase(){
        return restaurantPickedCollection.orderBy("name").get();
    }

    //---- DELETE ----
    public Task<Void> deleteRestaurant(String uid){
        return restaurantPickedCollection.document(uid).delete();
    }

    public void updateRestaurantRepository(RestaurantPicked restaurantPicked){
        this.restaurantPicked = restaurantPicked;
    }

}
