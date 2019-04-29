package com.galou.go4lunch.workmates;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galou.go4lunch.databinding.WorkmatesItemRecyclerViewBinding;
import com.galou.go4lunch.models.User;


/**
 * Created by galou on 2019-04-27
 */
public class WorkmatesRecyclerViewViewHolder extends RecyclerView.ViewHolder {

    private WorkmatesItemRecyclerViewBinding binding;

    public WorkmatesRecyclerViewViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void updateWithUser(User user){

    }

}
