package com.galou.go4lunch.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galou on 2019-04-23
 */
public class Restaurant {

    private String uid;
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
    private int openingHours;
    private Integer distance;
    private String urlPhoto;
    private int rating;
    private List<User> usersEatingHere;

    public Restaurant() { }

    public Restaurant(String uid, String name, Double latitude, Double longitude, @Nullable String address, @Nullable int openingHours, @Nullable String urlPhoto, @Nullable int rating) {
        this.uid = uid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.openingHours = openingHours;
        this.urlPhoto = urlPhoto;
        this.rating = rating;
        usersEatingHere = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(int openingHours) {
        this.openingHours = openingHours;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public void addUser(User user){
        usersEatingHere.add(user);
    }

    public List<User> getUsersEatingHere(){
        return usersEatingHere;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
