package com.galou.go4lunch.restaurantsList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.galou.go4lunch.R;
import com.galou.go4lunch.models.Restaurant;

import java.util.List;

/**
 * Created by galou on 2019-05-09
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private RequestManager glide;

    public RestaurantAdapter(List<Restaurant> restaurants, RequestManager glide) {
        this.restaurants = restaurants;
        this.glide = glide;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.updateRestaurantInfo(restaurants.get(position), this.glide);

    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.resto_item_recycler_view, parent, false);
        return new RestaurantViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    void update(List<Restaurant> restaurants){
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }
    
    Restaurant getRestaurant(int position) {
        return this.restaurants.get(position);
    }

}
