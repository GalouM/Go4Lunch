package com.galou.go4lunch.restaurantsList;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.galou.go4lunch.R;
import com.galou.go4lunch.databinding.FragmentListViewBinding;
import com.galou.go4lunch.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseRestaurantsListFragment {

    private RecyclerView recyclerView;
    private List<Restaurant> restaurants;
    private RestaurantAdapter adapter;

    private FragmentListViewBinding binding;


    public ListViewFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        this.configureRecycleView(view);
        this.configureBindingAndViewModel(view);
        this.createViewModelConnections();
        viewModel.requestListRestaurants();
        return view;
    }

    private void configureRecycleView(View view){
        restaurants = new ArrayList<>();
        adapter = new RestaurantAdapter(restaurants, Glide.with(this));
        recyclerView = view.findViewById(R.id.recycler_view_resto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------
    private void configureBindingAndViewModel(View view) {
        binding = FragmentListViewBinding.bind(view);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());


    }


    // --------------------
    // ACTION FROM VIEWMODEL
    // --------------------
    @Override
    public void displayRestaurants(List<Restaurant> restaurants){
        this.restaurants = restaurants;
        adapter.update(this.restaurants);

    }

}