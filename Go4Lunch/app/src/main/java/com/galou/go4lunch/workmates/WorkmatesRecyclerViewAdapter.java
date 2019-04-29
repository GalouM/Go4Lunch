package com.galou.go4lunch.workmates;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;

import java.util.List;

/**
 * Created by galou on 2019-04-27
 */
public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkmatesRecyclerViewViewHolder> {

    private List<User> users;
    private RequestManager glide;

    public WorkmatesRecyclerViewAdapter(List<User> users, RequestManager glide) {
        this.users = users;
        this.glide = glide;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesRecyclerViewViewHolder holder, int position) {
        holder.updateWithUser(users.get(position), this.glide);

    }

    @NonNull
    @Override
    public WorkmatesRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmates_item_recycler_view, parent, false);
        return new WorkmatesRecyclerViewViewHolder(view);
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
