package com.galou.go4lunch.workmates;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.models.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> users;
    private WorkmatesRecyclerViewAdapter adapter;

    private WorkmatesViewModel viewModel;


    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        configureRecycleView(view);
        viewModel = obtainViewModel();
        setupListUsers();
        viewModel.fetchListUsersFromFirebase();
        return view;
    }

    private WorkmatesViewModel obtainViewModel() {
        return ViewModelProviders.of(this)
                .get(WorkmatesViewModel.class);
    }

    private void setupListUsers(){
        viewModel.getUsers().observe(this, this::showUsers);
    }


    private void configureRecycleView(View view){
        users = new ArrayList<>();
        adapter = new WorkmatesRecyclerViewAdapter(users, Glide.with(this));
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void showUsers(List<User> users){
        this.users = users;
        adapter.update(this.users);

    }
}
