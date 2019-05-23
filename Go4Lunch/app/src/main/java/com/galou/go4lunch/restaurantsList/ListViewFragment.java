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
import com.galou.go4lunch.restoDetails.RestoDetailDialogFragment;
import com.galou.go4lunch.util.ItemClickSupport;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static com.galou.go4lunch.util.PositionUtil.convertLocationForApi;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseRestaurantsListFragment {

    private RecyclerView recyclerView;
    private List<Restaurant> restaurants;
    private RestaurantAdapter adapter;

    private FragmentListViewBinding binding;


    public ListViewFragment() {}

    // --------------------
    // LIFE CYCLE SATE
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        this.configureRecycleView(view);
        this.configureBindingAndViewModel(view);
        this.createViewModelConnections();
        viewModel.requestListRestaurants();
        return view;
    }
    
    // --------------------
    // CONFIGURE UI
    // --------------------

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

    @Override
    protected void setupLocation() {
        viewModel.setupLocation(locationUser);
        viewModel.requestListRestaurants();

    }

    // --------------------
    // ACTION FROM VIEWMODEL
    // --------------------
    @Override
    public void displayRestaurants(List<Restaurant> restaurants){
        this.restaurants = restaurants;
        adapter.update(this.restaurants);
        configureOnClickRecyclerView();

    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.resto_item_recycler_view)
                .setOnItemClickListener((recyclerView, position, v)
                        -> viewModel.updateRestaurantSelected(adapter.getRestaurant(position).getUid()));

    }



}
