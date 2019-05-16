package com.galou.go4lunch.restoDetails;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;

/**
 * Created by galou on 2019-05-15
 */
public class RestaurantDetailRVViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private Resources res;

    RestaurantDetailRVViewHolder(@NonNull View itemView) {
        super(itemView);
        res = itemView.getResources();
        textView = (TextView) itemView.findViewById(R.id.recycler_view_text);
        imageView = (ImageView) itemView.findViewById(R.id.recycler_view_image_user);
    }

    void updateWithUser(User user, RequestManager glide){

        textView.setText(String.format(res.getString(R.string.is_joining), user.getUsername()));

        if(user.getUrlPicture() != null){
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);
        }



    }
}
