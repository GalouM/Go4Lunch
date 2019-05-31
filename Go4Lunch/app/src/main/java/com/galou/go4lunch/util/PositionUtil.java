package com.galou.go4lunch.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

/**
 * Created by galou on 2019-05-09
 */
public abstract class PositionUtil {

    public static String convertLocationForApi(LatLng position){
        if(position != null) {
            Double lat = position.latitude;
            Double lng = position.longitude;

            return lat.toString() + "," + lng.toString();
        }
        return null;
    }

    public static LatLngBounds convertToBounds(LatLng center, double radius){
        double distanceFromCenter = radius * Math.sqrt(2.0);
        LatLng southWest = SphericalUtil.computeOffset(center, distanceFromCenter, 225.0);
        LatLng northEast = SphericalUtil.computeOffset(center, distanceFromCenter, 45.0);
        return new LatLngBounds(southWest, northEast);
    }



}
