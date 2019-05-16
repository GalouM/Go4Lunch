package com.galou.go4lunch.util;

import android.content.res.Resources;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.galou.go4lunch.R;


/**
 * Created by galou on 2019-05-15
 */
public class RatingBindingAdapter {
    @BindingAdapter("bind:displayRating")
    public static void loadImageCircle(ImageView view, int rating){
        Resources res = view.getResources();
        if(view.getId() == R.id.first_star) {
            view.setImageDrawable(res.getDrawable(RatingUtil.displayFirstStar(rating)));
        }
        if(view.getId() == R.id.second_star) {
            view.setImageDrawable(res.getDrawable(RatingUtil.displaySecondStar(rating)));
        }
        if(view.getId() == R.id.third_star) {
            view.setImageDrawable(res.getDrawable(RatingUtil.displayThirdStar(rating)));
        }
    }
}
