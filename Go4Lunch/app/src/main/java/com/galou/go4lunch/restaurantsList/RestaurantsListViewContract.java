package com.galou.go4lunch.restaurantsList;

import com.galou.go4lunch.models.Restaurant;

import java.util.List;

/**
 * Created by galou on 2019-05-09
 */
public interface RestaurantsListViewContract {
    void displayRestaurants(List<Restaurant> restaurants);
    void displayRestaurantDetail();
}
