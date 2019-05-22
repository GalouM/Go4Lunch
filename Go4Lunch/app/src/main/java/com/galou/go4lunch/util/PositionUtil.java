package com.galou.go4lunch.util;

import com.google.android.gms.maps.model.LatLng;

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


}
