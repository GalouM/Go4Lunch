package com.galou.go4lunch.restoDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.galou.go4lunch.base.ButtonActionListener;
import com.galou.go4lunch.databinding.FragmentItemListDialogBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.workmates.WorkmatesRecyclerViewAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galou.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     RestoDetailDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class RestoDetailDialogFragment extends BottomSheetDialogFragment implements RestaurantDetailContract {
    private RestaurantDetailViewModel viewModel;
    private FragmentItemListDialogBinding binding;
    private List<User> users;
    private RestaurantDetailRVAdapter adapter;
    private RecyclerView recyclerView;

    // --------------------
    // LIFE CYCLE SATE
    // --------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_dialog, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.destroyDisposable();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        configureBindingAndViewModel(view);
        configureRecycleView(view);

    }

    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------

    private void configureBindingAndViewModel(View view) {
        binding = FragmentItemListDialogBinding.bind(view);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        ButtonActionListener buttonActionListener = getButtonActionListener();
        binding.setListener(buttonActionListener);
        setupOpenPhoneIntent();
        setupOpenWebsite();
        setupUsers();
        viewModel.fetchInfoRestaurant();
        viewModel.configureSaveDataRepo(getContext());

    }

    private RestaurantDetailViewModel obtainViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(RestaurantDetailViewModel.class);
    }

    private void setupOpenPhoneIntent(){
        viewModel.getPhoneNumber().observe(this, this::openPhoneIntent);
    }

    private void setupOpenWebsite(){
        viewModel.getWebSite().observe(this, this::openWebViewIntent);
    }

    private void setupUsers(){
        viewModel.getUsers().observe(this, this::showUsers);
    }

    private ButtonActionListener getButtonActionListener(){
        return view -> {
            int id = view.getId();
            switch (id){
                case R.id.phone_button:
                    viewModel.fetchPhoneNumber();
                    break;
                case R.id.like_button:
                    viewModel.updateRestaurantLiked();
                    break;
                case R.id.website_button:
                    viewModel.fetchWebsite();
                    break;
                case R.id.pick_restaurant_button:
                    viewModel.updatePickedRestaurant();
                    break;
            }

        };
    }

    // --------------------
    // ACTION FROM VIEWMODEL
    // --------------------

    @Override
    public void openPhoneIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(String.format("tel:%s", phoneNumber)));
        startActivity(intent);
    }

    @Override
    public void openWebViewIntent(String urlWebsite) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlWebsite));
        startActivity(intent);


    }

    @Override
    public void saveRestaurantPicked(String id) {

    }

    @Override
    public void showUsers(List<User> users){
        this.users = users;
        adapter.update(this.users);

    }

    // --------------------
    // CONFIGURE UI
    // --------------------

    private void configureRecycleView(View view){
        users = new ArrayList<>();
        adapter = new RestaurantDetailRVAdapter(users, Glide.with(this));
        recyclerView = view.findViewById(R.id.recycler_view_detail_resto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }






}
