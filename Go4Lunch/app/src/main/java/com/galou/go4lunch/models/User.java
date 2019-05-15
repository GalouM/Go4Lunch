package com.galou.go4lunch.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galou on 2019-04-22
 */
public class User {

    private String uid;
    private String username;
    private String email;
    @Nullable private String urlPicture;
    @Nullable private Restaurant restaurantPicked;
    private List<String> likedRestaurantUuid;

    public User(){}

    public User(String uid, String username, String email, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.urlPicture = urlPicture;
        likedRestaurantUuid = new ArrayList<>();
    }

    //-----------
    // SETTERS & GETTERS
    //-----------

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public Restaurant getRestaurantPicked() {
        return restaurantPicked;
    }

    public void setRestaurantPicked(@Nullable Restaurant restaurantPicked) {
        this.restaurantPicked = restaurantPicked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getLikedRestaurantUuid() {
        return likedRestaurantUuid;
    }

    public void setLikedRestaurantUuid(List<String> likedRestaurantUuid) {
        this.likedRestaurantUuid = likedRestaurantUuid;
    }
}
