package com.galou.go4lunch.util;

import com.galou.go4lunch.R;

/**
 * Created by galou on 2019-05-13
 */
public abstract class RatingUtil {

    public static int calculateRating(Double rating){
        if (rating == 0) return 0;
        if (rating < 0.84) return 1;
        if (rating < 1.68) return 2;
        if (rating < 2.52) return 3;
        if (rating < 3.36) return 4;
        if (rating < 4.7) return 5;
        return 6;
    }

    public static int displayFirstStar(int rating){
        if (rating <= 0) return R.drawable.baseline_star_border_24;
        if(rating == 1) return R.drawable.baseline_star_half_24;
        return R.drawable.baseline_star_24;

    }

    public static int displaySecondStar(int rating) {
        if (rating <= 2) return R.drawable.baseline_star_border_24;
        if (rating == 3) return R.drawable.baseline_star_half_24;
        return R.drawable.baseline_star_24;
    }

    public static int displayThirdStar(int rating) {
        if (rating <= 4) return R.drawable.baseline_star_border_24;
        if (rating == 5) return R.drawable.baseline_star_half_24;
        return R.drawable.baseline_star_24;
    }


}
