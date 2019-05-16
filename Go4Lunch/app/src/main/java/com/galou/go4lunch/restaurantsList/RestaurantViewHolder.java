package com.galou.go4lunch.restaurantsList;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.galou.go4lunch.R;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.util.OpeningHoursUtil;
import com.galou.go4lunch.util.RatingUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by galou on 2019-05-09
 */
public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView typeAdress;
    private TextView numberPpl;
    private TextView openingHours;
    private TextView distance;
    private ImageView imageView;
    private Resources res;
    private ImageView ratingStar1;
    private ImageView ratingStar2;
    private ImageView ratingStar3;

    private String formatTimeDisplay;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        res = itemView.getResources();
        name = (TextView) itemView.findViewById(R.id.name_resto);
        typeAdress = (TextView) itemView.findViewById(R.id.address_type_resto);
        numberPpl = (TextView) itemView.findViewById(R.id.number_ppl_resto);
        openingHours = (TextView) itemView.findViewById(R.id.opening_hours_resto);
        distance = (TextView) itemView.findViewById(R.id.distance_resto);
        imageView = (ImageView) itemView.findViewById(R.id.image_resto);
        formatTimeDisplay = res.getString(R.string.format_time_display);
        ratingStar1 = (ImageView) itemView.findViewById(R.id.rating_star1);
        ratingStar2 = (ImageView) itemView.findViewById(R.id.rating_star2);
        ratingStar3 = (ImageView) itemView.findViewById(R.id.rating_star3);

    }

    public void updateRestaurantInfo(Restaurant restaurant, RequestManager glide){
        name.setText(restaurant.getName());

        String address = restaurant.getAddress();
        typeAdress.setText(address);

        String distanceToDisplay = "0m";
        if(restaurant.getDistance() != null){
            distanceToDisplay = String.format("%sm", restaurant.getDistance().toString());
        }
        distance.setText(distanceToDisplay);

        if(restaurant.getUrlPhoto() != null){
            glide.load(restaurant.getUrlPhoto()).into(imageView);
        }

        numberPpl.setText(String.format("(%d)", restaurant.getUsersEatingHere().size()));

        glide.load(restaurant.getUrlPhoto()).apply(RequestOptions.centerCropTransform()).into(imageView);

        this.displayOpeningHours(restaurant);
        this.displayRating(restaurant);




    }

    private void displayOpeningHours(Restaurant restaurant){
        int timeOpening = restaurant.getOpeningHours();
        switch (timeOpening){
            case R.string.closed:
                openingHours.setText(timeOpening);
                TextViewCompat.setTextAppearance(openingHours, R.style.TimeRestaurantClosed);
                break;
            case R.string.closing_soon:
                openingHours.setText(timeOpening);
                TextViewCompat.setTextAppearance(openingHours, R.style.TimeRestaurantClosingSoon);
                break;
            case R.string.no_time:
                openingHours.setText(timeOpening);
                TextViewCompat.setTextAppearance(openingHours, R.style.TimeRestaurantOpen);
                break;
            case R.string.open_24_7:
                openingHours.setText(timeOpening);
                TextViewCompat.setTextAppearance(openingHours, R.style.TimeRestaurantOpen);
                break;
            default:
                DateFormat dateFormat = new SimpleDateFormat(formatTimeDisplay);
                String timeToDisplay = dateFormat.format(OpeningHoursUtil.convertStringInDate(timeOpening));
                openingHours.setText(String.format(res.getString(R.string.open_until), timeToDisplay));
                TextViewCompat.setTextAppearance(openingHours, R.style.TimeRestaurantOpen);
                break;


        }

    }

    private void displayRating(Restaurant restaurant){
        int rating = restaurant.getRating();
        ratingStar1.setImageDrawable(res.getDrawable(RatingUtil.displayFirstStar(rating)));
        ratingStar2.setImageDrawable(res.getDrawable(RatingUtil.displaySecondStar(rating)));
        ratingStar3.setImageDrawable(res.getDrawable(RatingUtil.displayThirdStar(rating)));

    }
}