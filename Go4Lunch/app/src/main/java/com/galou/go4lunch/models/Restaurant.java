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
    private String phoneNumber;
    private String webSite;
    private List<User> usersEatingHere;

    public Restaurant(String uid, String name, Double latitude, Double longitude, @Nullable String address, @Nullable int openingHours, @Nullable String urlPhoto, @Nullable int rating, String phoneNumber, String webSite) {
        this.uid = uid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.openingHours = openingHours;
        this.urlPhoto = urlPhoto;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
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

    public void removeUser(User userToDelete){
        String uidUser = userToDelete.getUid();
        int position = 0;
        for(User user : usersEatingHere){
            if(user.getUid().equals(uidUser)){
                usersEatingHere.remove(position);
                break;
            }
            position += 1;
        }
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }
}
