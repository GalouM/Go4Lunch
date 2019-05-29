package com.galou.go4lunch.restoDetails;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;

import java.util.List;

/**
 * Created by galou on 2019-05-15
 */
public class RestaurantDetailRVAdapter extends RecyclerView.Adapter<RestaurantDetailRVViewHolder> {

    private List<User> users;
    private RequestManager glide;

    public RestaurantDetailRVAdapter(List<User> users, RequestManager glide)  {
        this.users = users;
        this.glide = glide;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailRVViewHolder holder, int position) {
        holder.updateWithUser(users.get(position), this.glide);

    }

    @NonNull
    @Override
    public RestaurantDetailRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.resto_detail_user_item_recycler_view, parent, false);
        return new RestaurantDetailRVViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    void update(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }
}
