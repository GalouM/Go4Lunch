package com.galou.go4lunch.models;

public class RestaurantPicked {
    private String uid;
    private String name;
    private String photoUrl;
    private String address;
    private int rating;
    private String website;
    private String phoneNumber;
    private String datePicked;

    public RestaurantPicked(String uid, String name, String photoUrl, String address, int rating, String website, String phoneNumber, String datePicked){
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.address = address;
        this.rating = rating;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.datePicked = datePicked;
    }
}
