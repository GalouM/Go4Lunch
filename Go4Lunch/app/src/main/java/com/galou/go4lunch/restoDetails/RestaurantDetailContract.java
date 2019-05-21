package com.galou.go4lunch.restoDetails;

import com.galou.go4lunch.models.User;

import java.util.List;

/**
 * Created by galou on 2019-05-15
 */
public interface RestaurantDetailContract {

    void openPhoneIntent(String phoneNumber);
    void openWebViewIntent(String urlWebsite);
    void showUsers(List<User> users);
    void saveRestaurantPicked(String id);
}
