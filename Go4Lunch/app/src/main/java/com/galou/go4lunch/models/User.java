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
    @Nullable private String restaurant;
    private List<String> likedRestaurants;

    public User(){}

    public User(String uid, String username, String email, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.email = email;
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
    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(@Nullable String restaurant) {
        this.restaurant = restaurant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getLikedRestaurants() {
        return likedRestaurants;
    }

    public void addLikedRestaurant(String restaurantUid){
        if(likedRestaurants == null) {
            this.likedRestaurants = new ArrayList<>();
        }
        this.likedRestaurants.add(restaurantUid);
    }

    public void removeLikedRestaurant(String restaurantUid){
        if(likedRestaurants != null) {
            int position = 0;
            for (String uid : likedRestaurants) {
                if (uid.equals(restaurantUid)) likedRestaurants.remove(position);
                position += 1;
            }
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", urlPicture='" + urlPicture + '\'' +
                ", restaurant='" + restaurant + '\'' +
                ", likedRestaurants=" + likedRestaurants +
                '}';
    }
}
