package com.galou.go4lunch.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;

/**
 * Created by galou on 2019-04-27
 */
public class WorkmatesRecyclerViewAdapter extends FirestoreRecyclerAdapter<User, WorkmatesRecyclerViewViewHolder> {

    public WorkmatesRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

<<<<<<< HEAD
=======
    @Override
    protected void onBindViewHolder(@NonNull WorkmatesRecyclerViewViewHolder holder, int i, @NonNull User user) {
        holder.updateWithUser(user);
<<<<<<< HEAD
>>>>>>> parent of 0ec76f8... workmates in mvvm
=======
>>>>>>> parent of 0ec76f8... workmates in mvvm

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesRecyclerViewViewHolder holder, int i, @NonNull User user) {
        holder.updateWithUser(user);
    }

    @NonNull
    @Override
    public WorkmatesRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmates_item_recycler_view, parent, false);
        return new WorkmatesRecyclerViewViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workmates_item_recycler_view, parent, false));
    }


    @Override
    public int getItemCount() {
        return 0;
    }
}
