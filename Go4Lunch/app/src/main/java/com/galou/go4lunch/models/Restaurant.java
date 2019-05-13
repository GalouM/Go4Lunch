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
    private String type;
    private String address;
    private String openingHours;
    private String closureHours;
    private Integer distance;
    private String urlPhoto;
    private List<User> usersEatingHere;

    public Restaurant() { }

    public Restaurant(String uid, String name, Double latitude, Double longitude, @Nullable String type, @Nullable String address, @Nullable String openingHours, @Nullable String closureHours) {
        this.uid = uid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.address = address;
        this.openingHours = openingHours;
        this.closureHours = closureHours;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
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

    public String getClosureHours() {
        return closureHours;
    }

    public void setClosureHours(String closureHours) {
        this.closureHours = closureHours;
    }
}
