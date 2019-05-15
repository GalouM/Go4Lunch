package com.galou.go4lunch.restoDetails;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.galou.go4lunch.databinding.FragmentItemListDialogBinding;
import com.galou.go4lunch.databinding.FragmentWorkmatesBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.workmates.WorkmatesViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galou.go4lunch.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     RestoDetailDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class RestoDetailDialogFragment extends BottomSheetDialogFragment {
    private RestaurantDetailViewModel viewModel;
    private FragmentItemListDialogBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        configureBindingAndViewModel(view);
        viewModel.fetchInfoRestaurant();

    }

    private void configureBindingAndViewModel(View view) {
        binding = FragmentItemListDialogBinding.bind(view);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());

    }

    private RestaurantDetailViewModel obtainViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(RestaurantDetailViewModel.class);
    }




}
