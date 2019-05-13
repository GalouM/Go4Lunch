package com.galou.go4lunch.util;

import android.util.Log;

import com.galou.go4lunch.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by galou on 2019-05-12
 */
public abstract class OpeningHoursUtil {

    private static String FORMAT_HOURS = "HHmm";

    public static int getOpeningText(String openingHours, String closureHours){
        if(openingHours == null || closureHours == null){
            return R.string.no_time;
        }
        int opening = Integer.parseInt(openingHours);
        int closure = Integer.parseInt(closureHours);
        Date todayDate = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_HOURS);
        String todayDateString = dateFormat.format(todayDate);
        int todayInt = Integer.parseInt(todayDateString);
        
        if(opening == closure){
            return R.string.open_24h;
        }

        if (todayInt >= opening && todayInt <= closure) {
            int timeBeforeClosure = closure - todayInt;
            if(timeBeforeClosure <= 100){
                return R.string.closing_soon;
            } else {
                return R.string.open_until;
            }
        }
        if (todayInt < opening){
            int timeBeforeOpening = opening - todayInt;
            if(timeBeforeOpening <= 100){
                return R.string.opening_soon;
            }
        }

        return R.string.closed;
    }

    public static Date convertStringInDate(String hour){
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_HOURS);
        try {
            return dateFormat.parse(hour);
        } catch (ParseException e) {
            return null;
        }
    }
}
