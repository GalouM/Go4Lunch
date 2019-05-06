package com.galou.go4lunch.workmates;

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
 * Created by galou on 2019-04-27
 */
public class WorkmatesRecyclerViewViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private Resources res;

    WorkmatesRecyclerViewViewHolder(@NonNull View itemView) {
        super(itemView);
        res = itemView.getResources();
        textView = (TextView) itemView.findViewById(R.id.recycler_view_text);
        imageView = (ImageView) itemView.findViewById(R.id.recycler_view_image_user);
    }

    void updateWithUser(User user, RequestManager glide){
        String textToDisplay;
        if(user.getRestaurantPicked() != null){
            textToDisplay = String.format(res.getString(R.string.display_text_user_list),
                    user.getUsername(),
                    user.getRestaurantPicked().getType(),
                    user.getRestaurantPicked().getName());

        } else {
            textToDisplay = String.format(res.getString(R.string.display_text_user_list_not_decided), user.getUsername());
            TextViewCompat.setTextAppearance(textView, R.style.UserNotDecidedText);
        }
        textView.setText(textToDisplay);

        if(user.getUrlPicture() != null){
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);
        }



    }

}
