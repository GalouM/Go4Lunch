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
public class UserUnitTest {
    private User user;
    private String name;
    private String uid;
    private String email;
    private String photoUrl;
    private String restaurantUid;
    private String restaurantName;
    private String restaurantAddress;

    private String likedRestaurant1;
    private String likedRestaurant2;
    private String likedRestaurant3;

    private List<String> likedRestaurants;

    @Before
    public void setup(){
        name = "Jean Michel";
        uid = "12345";
        photoUrl = "http://photo";
        email = "name@email.com";
        user = new User(uid, name, email, photoUrl);
        restaurantUid = "54321";
        restaurantName = "nameResto";
        restaurantAddress = "123 adresse Resto";
        likedRestaurant1 = "uid1";
        likedRestaurant2 = "uid2";
        likedRestaurant3 = "uid3";
        likedRestaurants = new ArrayList<>();
        likedRestaurants.add(likedRestaurant1);
        likedRestaurants.add(likedRestaurant2);
        likedRestaurants.add(likedRestaurant3);
    }

    @Test
    public void getCorrectInfoFromUser() throws Exception{
        assertEquals(uid, user.getUid());
        assertEquals(name, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(photoUrl, user.getUrlPicture());
    }

    @Test
    public void changeInfoUser_getCorrectInfo() throws Exception{
        String newName = "Michel Jean";
        String newUid = "4321";
        String newPhotoUrl = "http://newphoto";
        String newEmail = "new@email.com";
        user.setUsername(newName);
        user.setUid(newUid);
        user.setUrlPicture(newPhotoUrl);
        user.setEmail(newEmail);
        user.setRestaurantUid(restaurantUid);
        user.setRestaurantName(restaurantName);
        user.setRestaurantAddress(restaurantAddress);
        user.addLikedRestaurant(likedRestaurant1);
        user.addLikedRestaurant(likedRestaurant2);
        user.addLikedRestaurant(likedRestaurant3);


        assertEquals(newUid, user.getUid());
        assertEquals(newName, user.getUsername());
        assertEquals(newEmail, user.getEmail());
        assertEquals(newPhotoUrl, user.getUrlPicture());
        assertEquals(restaurantUid, user.getRestaurantUid());
        assertEquals(restaurantName, user.getRestaurantName());
        assertEquals(restaurantAddress, user.getRestaurantAddress());
        assertEquals(likedRestaurants, user.getLikedRestaurants());
    }

}
