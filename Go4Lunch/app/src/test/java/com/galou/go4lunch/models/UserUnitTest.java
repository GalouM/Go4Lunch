package com.galou.go4lunch.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
    private String photoUrl;

    @Before
    public void setup(){
        name = "Jean Michel";
        uid = "12345";
        photoUrl = "http://photo";
        restaurant = new Restaurant();
        user = new User(uid, name, photoUrl);
    }

    @Test
    public void getCorrectInfoFromUser() throws Exception{
        assertEquals(uid, user.getUid());
        assertEquals(name, user.getUsername());
        assertEquals(photoUrl, user.getUrlPicture());
    }

    @Test
    public void changeInfoUser_getCorrectInfo() throws Exception{
        String newName = "Michel Jean";
        String newUid = "4321";
        String newPhotoUrl = "http://newphoto";
        user.setUsername(newName);
        user.setRestaurantPicked(restaurant);
        user.setUid(newUid);
        user.setUrlPicture(newPhotoUrl);

        assertEquals(newUid, user.getUid());
        assertEquals(newName, user.getUsername());
        assertEquals(newPhotoUrl, user.getUrlPicture());
        assertEquals(restaurant, user.getRestaurantPicked());
    }

}
