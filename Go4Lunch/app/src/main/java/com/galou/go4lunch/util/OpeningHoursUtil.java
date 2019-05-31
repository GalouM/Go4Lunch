package com.galou.go4lunch.util;

import android.util.Log;

import com.galou.go4lunch.R;
import com.galou.go4lunch.models.OpeningHoursApiPlace;
import com.google.android.libraries.places.api.internal.impl.net.pablo.PlaceResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by galou on 2019-05-12
 */
public abstract class OpeningHoursUtil {

    private static String FORMAT_HOURS = "HHmm";


    public static Date convertStringInDate(int hour){
        String hourInString = String.valueOf(hour);
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_HOURS);
        try {
            return dateFormat.parse(hourInString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int getOpeningTime(OpeningHoursApiPlace openingHours){
        if(openingHours == null || openingHours.getPeriods() == null) return R.string.no_time;
        if(openingHours.getOpenNow() != null && !openingHours.getOpenNow()){
            return R.string.closed;
        }

        int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -1;
        if(openingHours.getPeriods().size() >= dayOfTheWeek+1){
            PlaceResult.OpeningHours.Period periodOfTheDay = openingHours.getPeriods().get(dayOfTheWeek);

            if(periodOfTheDay.getClose() == null) return R.string.open_24_7;

            String closureString = periodOfTheDay.getClose().getTime();
            int closure = Integer.parseInt(closureString);

            Date todayDate = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat(FORMAT_HOURS);
            String todayDateString = dateFormat.format(todayDate);
            int timeNow = Integer.parseInt(todayDateString);
            int timeBeforeClosure = closure - timeNow;
            if(timeBeforeClosure <= 100){
                return R.string.closing_soon;
            } else {
                return closure;
            }


        }
        return R.string.no_time;

    }

}
