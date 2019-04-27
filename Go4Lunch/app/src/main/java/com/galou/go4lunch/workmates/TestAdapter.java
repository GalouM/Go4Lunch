package com.galou.go4lunch.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;

import java.util.List;

/**
 * Created by galou on 2019-04-27
 */
public class TestAdapter extends RecyclerView.Adapter<WorkmatesRecyclerViewViewHolder> {

    private List<User> users;

    public TestAdapter(List<User> users) {
        this.users = users;
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
    public void onBindViewHolder(@NonNull WorkmatesRecyclerViewViewHolder holder, int position) {
        holder.updateWithUser(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
