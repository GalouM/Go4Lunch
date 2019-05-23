package com.galou.go4lunch.util;

import android.location.Location;

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
        double distancefromCenter = radius * Math.sqrt(2.0);
        LatLng soutWest = SphericalUtil.computeOffset(center, distancefromCenter, 225.0);
        LatLng nortEast = SphericalUtil.computeOffset(center, distancefromCenter, 45.0);
        return new LatLngBounds(soutWest, nortEast);
    }



}
