package com.galou.go4lunch.models.utils;

import com.galou.go4lunch.R;
import com.galou.go4lunch.util.RatingUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.galou.go4lunch.util.RatingUtil.displayFirstStar;
import static com.galou.go4lunch.util.RatingUtil.displaySecondStar;
import static com.galou.go4lunch.util.RatingUtil.displayThirdStar;
import static org.junit.Assert.assertEquals;

/**
 * Created by galou on 2019-05-30
 */
@RunWith(JUnit4.class)
public class RatingUtilUnitTest {

    @Test
    public void calculateRating_returnCorrectValue() throws Exception{
        assertEquals(0, RatingUtil.calculateRating(0.0));
        assertEquals(1, RatingUtil.calculateRating(0.8));
        assertEquals(2, RatingUtil.calculateRating(1.5));
        assertEquals(3, RatingUtil.calculateRating(2.0));
        assertEquals(4, RatingUtil.calculateRating(3.0));
        assertEquals(5, RatingUtil.calculateRating(4.0));
        assertEquals(6, RatingUtil.calculateRating(5.0));

    }

    @Test
    public void valueRating_returnRightValueStar() throws Exception{
        int rating = 0;
        assertEquals(R.drawable.baseline_star_border_24, displayFirstStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displaySecondStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displayThirdStar(rating));

        rating = 1;
        assertEquals(R.drawable.baseline_star_half_24, displayFirstStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displaySecondStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displayThirdStar(rating));

        rating = 2;
        assertEquals(R.drawable.baseline_star_24, displayFirstStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displaySecondStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displayThirdStar(rating));

        rating = 3;
        assertEquals(R.drawable.baseline_star_24, displayFirstStar(rating));
        assertEquals(R.drawable.baseline_star_half_24, displaySecondStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displayThirdStar(rating));

        rating = 4;
        assertEquals(R.drawable.baseline_star_24, displayFirstStar(rating));
        assertEquals(R.drawable.baseline_star_24, displaySecondStar(rating));
        assertEquals(R.drawable.baseline_star_border_24, displayThirdStar(rating));

        rating = 5;
        assertEquals(R.drawable.baseline_star_24, displayFirstStar(rating));
        assertEquals(R.drawable.baseline_star_24, displaySecondStar(rating));
        assertEquals(R.drawable.baseline_star_half_24, displayThirdStar(rating));

        rating = 6;
        assertEquals(R.drawable.baseline_star_24, displayFirstStar(rating));
        assertEquals(R.drawable.baseline_star_24, displaySecondStar(rating));
        assertEquals(R.drawable.baseline_star_24, displayThirdStar(rating));

    }

}
