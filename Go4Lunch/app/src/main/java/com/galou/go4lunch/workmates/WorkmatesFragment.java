package com.galou.go4lunch.workmates;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.galou.go4lunch.R;
import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.databinding.FragmentWorkmatesBinding;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.util.SnackBarUtil;
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
    private FrameLayout frameLayout;

    private WorkmatesViewModel viewModel;
    private FragmentWorkmatesBinding binding;


    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        this.configureRecycleView(view);
        this.configureForeground(view);
        this.configureBindingAndViewModel(view);
        viewModel.fetchListUsersFromFirebase();
        return view;
    }

    private void configureBindingAndViewModel(View view) {
        binding = FragmentWorkmatesBinding.bind(view);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        setupListUsers();
        setupForegroundAlpha();
        setupSnackBar();

    }

    private WorkmatesViewModel obtainViewModel() {
        return ViewModelProviders.of(this)
                .get(WorkmatesViewModel.class);
    }

    private void setupListUsers(){
        viewModel.getUsers().observe(this, this::showUsers);
    }

    private void setupForegroundAlpha(){
        viewModel.isLoading.observe(this, isLoading -> {
            if(isLoading){
                frameLayout.getForeground().setAlpha(50);
            } else {
                frameLayout.getForeground().setAlpha(0);
            }
        });
    }

    private void setupSnackBar(){
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(getView(), getString(message));
            }
        });

    }


    private void configureRecycleView(View view){
        users = new ArrayList<>();
        adapter = new WorkmatesRecyclerViewAdapter(users, Glide.with(this));
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void configureForeground(View view){
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        frameLayout.setForeground(new ColorDrawable(Color.BLACK));
        frameLayout.getForeground().setAlpha(0);
    }

    private void showUsers(List<User> users){
        this.users = users;
        adapter.update(this.users);

    }
}
