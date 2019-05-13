package com.galou.go4lunch.util;

import android.util.Log;

import com.galou.go4lunch.R;

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

    private static String FORMAT_HOURS = "hhmm";

    public static int getOpeningText(String openingHours, String closureHours){
        Date todayDate = Calendar.getInstance().getTime();
        Date openingDate = null;
        Date closureDate = null;
        Log.e("closing", closureHours);
        try {
            openingDate = new SimpleDateFormat(FORMAT_HOURS, Locale.CANADA).parse(openingHours);
            closureDate = new SimpleDateFormat(FORMAT_HOURS, Locale.CANADA).parse(closureHours);
            Log.e("clsong date", closureDate.toString());
            Log.e("today", todayDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (openingDate != null && closureDate != null) {
            if (todayDate.after(openingDate) && todayDate.before(closureDate)) {
                long timeBeforeClosure = TimeUnit.MILLISECONDS.toMinutes(Math.abs(todayDate.getTime() - closureDate.getTime()));
                if(timeBeforeClosure <= 60){
                    return R.string.closing_soon;
                } else {
                    return R.string.open_until;
                }

            } 
            if (openingDate.before(todayDate)){
                long timeBeforeOpening = TimeUnit.MILLISECONDS.toMinutes(Math.abs(openingDate.getTime() - todayDate.getTime()));
                if(timeBeforeOpening <= 60){
                    return R.string.opening_soon;
                } 
            }
            
            return R.string.closed;
        }
        
        return R.string.no_time;


    }
}
