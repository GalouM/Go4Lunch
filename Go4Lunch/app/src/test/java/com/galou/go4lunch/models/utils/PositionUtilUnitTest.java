package com.galou.go4lunch.models.utils;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.galou.go4lunch.util.PositionUtil.convertLocationForApi;
import static org.junit.Assert.assertEquals;

/**
 * Created by galou on 2019-05-30
 */
@RunWith(JUnit4.class)
public class PositionUtilUnitTest {

    @Test
    public void convertLocationForApi_correct() throws Exception{
        Double latitude = 23.456;
        Double longitude = -54.321;
        LatLng location = new LatLng(latitude, longitude);

        assertEquals(latitude.toString() + "," + longitude.toString(), convertLocationForApi(location));
    }
}
