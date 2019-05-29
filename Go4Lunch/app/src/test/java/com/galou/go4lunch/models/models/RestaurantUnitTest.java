package com.galou.go4lunch.models.models;

import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by galou on 2019-04-23
 */
@RunWith(JUnit4.class)
public class RestaurantUnitTest {

    private Restaurant restaurant;

    private String name;
    private String uid;
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
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setup(){
        name = "Jean Michel";
        uid = "12345";
        latitude = -123.455;
        longitude = 234.5354;
        address = "123 address";
        openingHours = 12345;
        distance = 3;
        urlPhoto = "http://photo";
        rating = 3;
        phoneNumber = "604-803-6789";
        webSite = "www.website.com";
        user1 = new User("123" , "name1", "email1", null);
        user2 = new User("456" , "name2", "email2", null);
        user3 = new User("789" , "name3", "email3", null);
        usersEatingHere = new ArrayList<>();
        usersEatingHere.add(user1);
        usersEatingHere.add(user2);
        usersEatingHere.add(user3);

        restaurant = new Restaurant(uid, name, latitude, longitude, address, openingHours, urlPhoto, rating, phoneNumber, webSite);
    }

    @Test
    public void getCorrectInfoFromRestaurant() throws Exception{
        assertEquals(uid, restaurant.getUid());
        assertEquals(name, restaurant.getName());
        assertEquals(latitude, restaurant.getLatitude());
        assertEquals(longitude, restaurant.getLongitude());
        assertEquals(address, restaurant.getAddress());
        assertEquals(openingHours, restaurant.getOpeningHours());
        assertEquals(urlPhoto, restaurant.getUrlPhoto());
        assertEquals(rating, restaurant.getRating());
        assertEquals(phoneNumber, restaurant.getPhoneNumber());
        assertEquals(webSite, restaurant.getWebSite());
    }

    @Test
    public void changeInfoUser_getCorrectInfo() throws Exception{
        restaurant.setUserGoingEating(usersEatingHere);

        assertEquals(usersEatingHere, restaurant.getUsersEatingHere());
    }
}
