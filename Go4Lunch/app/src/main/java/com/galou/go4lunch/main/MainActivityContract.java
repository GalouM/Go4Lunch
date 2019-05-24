package com.galou.go4lunch.main;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by galou on 2019-04-23
 */
public interface MainActivityContract {
    void logoutUser();
    void settings(Boolean setting);
    void displayRestaurantDetail();
    void configureNotification(boolean isEnable);
    void configureAutocomplete(LatLng position);
}
