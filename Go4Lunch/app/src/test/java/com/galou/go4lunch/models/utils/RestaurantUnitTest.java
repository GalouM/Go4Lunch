package com.galou.go4lunch.models.utils;

import com.galou.go4lunch.models.Restaurant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Created by galou on 2019-04-23
 */
@RunWith(JUnit4.class)
public class RestaurantUnitTest {
    private Restaurant restaurant;
    private String name;
    private String uid;
    private String type;

    @Before
    public void setup(){
        name = "Jean Michel";
        uid = "12345";
        type = "french";
        restaurant = new Restaurant(uid, name, type);
    }

    @Test
    public void getCorrectInfoFromRestaurant() throws Exception{
        assertEquals(uid, restaurant.getUid());
        assertEquals(name, restaurant.getName());
        assertEquals(type, restaurant.getType());
    }

    @Test
    public void changeInfoUser_getCorrectInfo() throws Exception{
        String newName = "Michel Jean";
        String newUid = "4321";
        String newType = "asian";
        restaurant.setName(newName);
        restaurant.setType(newType);
        restaurant.setUid(newUid);

        assertEquals(newUid, restaurant.getUid());
        assertEquals(newName, restaurant.getName());
        assertEquals(newType, restaurant.getType());
    }
}
