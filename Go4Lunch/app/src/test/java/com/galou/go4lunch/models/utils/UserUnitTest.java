package com.galou.go4lunch.models.utils;

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
    private Restaurant restaurant;
    private String name;
    private String uid;
    private String email;
    private String photoUrl;
    private List<String> likedRestaurants;

    @Before
    public void setup(){
        name = "Jean Michel";
        uid = "12345";
        photoUrl = "http://photo";
        restaurant = new Restaurant();
        email = "name@email.com";
        user = new User(uid, name, email, photoUrl);
        likedRestaurants = new ArrayList<>();
        likedRestaurants.add("uid1");
        likedRestaurants.add("uid2");
        likedRestaurants.add("uid3");
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
        user.setRestaurantPicked(restaurant);
        user.setUid(newUid);
        user.setUrlPicture(newPhotoUrl);
        user.setEmail(newEmail);
        user.setLikedRestaurantUuid(likedRestaurants);

        assertEquals(newUid, user.getUid());
        assertEquals(newName, user.getUsername());
        assertEquals(newEmail, user.getEmail());
        assertEquals(newPhotoUrl, user.getUrlPicture());
        assertEquals(restaurant, user.getRestaurantPicked());
        assertEquals(likedRestaurants, user.getLikedRestaurantUuid());
    }

}
