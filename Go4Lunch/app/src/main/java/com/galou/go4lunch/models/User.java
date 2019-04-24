package com.galou.go4lunch.models;

import androidx.annotation.Nullable;

/**
 * Created by galou on 2019-04-22
 */
public class User {

    private String uid;
    private String username;
    @Nullable private String urlPicture;
    @Nullable private Restaurant restaurantPicked;

    public User(){}

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
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
}
