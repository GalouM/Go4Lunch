package com.galou.go4lunch.workmates;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.galou.go4lunch.R;
import com.galou.go4lunch.databinding.FragmentWorkmatesBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.restoDetails.RestoDetailDialogFragment;
import com.galou.go4lunch.util.ItemClickSupport;
import com.galou.go4lunch.util.RetryAction;
import com.galou.go4lunch.util.SnackBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment implements WorkmateContract {

    private RecyclerView recyclerView;
    private List<User> users;
    private WorkmatesRecyclerViewAdapter adapter;

    private WorkmatesViewModel viewModel;
    private FragmentWorkmatesBinding binding;


    public WorkmatesFragment() {}

    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        this.configureRecycleView(view);
        this.configureBindingAndViewModel(view);
        viewModel.fetchListUsersFromFirebase();
        return view;
    }
    
    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------

    private void configureBindingAndViewModel(View view) {
        binding = FragmentWorkmatesBinding.bind(view);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        setupListUsers();
        setupSnackBar();
        setupSnackBarWithAction();
        setupOpenDetailRestaurant();

    }

    private WorkmatesViewModel obtainViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(WorkmatesViewModel.class);
    }

    private void setupListUsers(){
        viewModel.getUsers().observe(this, this::showUsers);
    }

    private void setupSnackBar(){
        viewModel.getSnackBarMessage().observe(this, messageEvent -> {
            Integer message = messageEvent.getContentIfNotHandle();
            if(message != null){
                SnackBarUtil.showSnackBar(getView(), getString(message));
            }
        });

    }

    private void setupSnackBarWithAction(){
        viewModel.getSnackBarWithAction().observe(this, actionEvent -> {
            RetryAction action = actionEvent.getContentIfNotHandle();
            if(action != null){
                SnackBarUtil.showSnackBarWithRetryButton(getView(), getString(R.string.error_unknown_error), viewModel, action);
            }
        });

    }

    private void setupOpenDetailRestaurant(){
        viewModel.getOpenDetailRestaurant().observe(this, openEvent -> {
            if(openEvent.getContentIfNotHandle() != null) {
                displayRestaurantDetail();
            }
        });
    }

    // --------------------
    // CONFIGURE UI
    // --------------------

    private void configureRecycleView(View view){
        users = new ArrayList<>();
        adapter = new WorkmatesRecyclerViewAdapter(users, Glide.with(this));
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        configureOnClickRecyclerView();
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.resto_item_recycler_view)
                .setOnItemClickListener((recyclerView, position, v)
                        -> viewModel.updateRestaurantToDisplay(adapter.getUser(position)));

    }

    // --------------------
    // ACTIONS FROM VIEW MODEL
    // --------------------

    @Override
    public void showUsers(List<User> users){
        this.users = users;
        adapter.update(this.users);
        this.configureOnClickRecyclerView();

    }

    @Override
    public void displayRestaurantDetail(){
        RestoDetailDialogFragment restoDetailDialogFragment = new RestoDetailDialogFragment();
        restoDetailDialogFragment.show(getActivity().getSupportFragmentManager(), "MODAL");
    }
}
