package com.galou.go4lunch.restaurantsList;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.galou.go4lunch.R;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.Result;

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

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        res = itemView.getResources();
        name = (TextView) itemView.findViewById(R.id.name_resto);
        typeAdress = (TextView) itemView.findViewById(R.id.address_type_resto);
        numberPpl = (TextView) itemView.findViewById(R.id.number_ppl_resto);
        openingHours = (TextView) itemView.findViewById(R.id.opening_hours_resto);
        distance = (TextView) itemView.findViewById(R.id.distance_resto);
        imageView = (ImageView) itemView.findViewById(R.id.image_resto);
    }

    public void updateRestaurantInfo(Restaurant restaurant, RequestManager glide){
        name.setText(restaurant.getName());
        String address = restaurant.getAddress();
        typeAdress.setText(address);
        if(restaurant.getUrlPhoto() != null){
            glide.load(restaurant.getUrlPhoto()).into(imageView);
        }
        numberPpl.setText(String.format("(%d)", restaurant.getUsersEatingHere().size()));

    }
}
