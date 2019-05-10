package com.galou.go4lunch.map;

import com.galou.go4lunch.models.Result;

import java.util.List;

/**
 * Created by galou on 2019-05-09
 */
public interface MapViewContract {
    void createMarkerForRestaurants(List<Result> restaurants);
}
