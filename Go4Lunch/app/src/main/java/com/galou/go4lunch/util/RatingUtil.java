package com.galou.go4lunch.util;

/**
 * Created by galou on 2019-05-13
 */
public abstract class RatingUtil {

    public static int calculateRating(Double rating){
        if (rating == 0) return 0;
        if (rating < 2) return 1;
        if (rating < 4) return 2;
        return 3;
    }

}
