package com.galou.go4lunch.restaurantsList;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.galou.go4lunch.R;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.restoDetails.RestoDetailDialogFragment;
import com.galou.go4lunch.util.SnackBarUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by galou on 2019-05-09
 */
public abstract class BaseRestaurantsListFragment extends Fragment implements RestaurantsListViewContract, EasyPermissions.PermissionCallbacks {

    protected RestaurantsListViewModel viewModel;

    // FOR GPS PERMISSION
    protected static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    protected static final int RC_LOCATION_PERMS = 100;

    // FOR LOCATION
    protected FusedLocationProviderClient fusedLocationClient;
    protected LatLng locationUser;

    public abstract void displayRestaurants(List<Restaurant> restaurants);
    protected abstract void setupLocation();

    protected void createViewModelConnections() {
        this.setupRestaurantDisplay();
        this.setupSnackBarWithAction();
        this.setupSnackBar();
        this.setupOpenDetailRestaurant();
        this.setupRequestLocation();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.destroyDisposable();
    }

    protected RestaurantsListViewModel obtainViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(RestaurantsListViewModel.class);
    }

    protected void setupRestaurantDisplay(){
        viewModel.getRestaurantsList().observe(this, this::displayRestaurants);
    }

    protected void setupSnackBarWithAction(){
        viewModel.getSnackBarWithAction().observe(this, action -> {
            if(action != null){
                SnackBarUtil.showSnackBarWithRetryButton(getView(), getString(R.string.error_unknown_error), viewModel, action);
            }
        });

    }

    protected void setupSnackBar(){
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(getView(), getString(message));
            }
        });

    }

    protected void setupOpenDetailRestaurant(){
        viewModel.getOpenDetailRestaurant().observe(this, open -> displayRestaurantDetail());
    }

    protected void setupRequestLocation(){
        viewModel.getRequestLocation().observe(this, request -> fetchLastKnowLocation());
    }

    @Override
    public void displayRestaurantDetail(){
        RestoDetailDialogFragment restoDetailDialogFragment = new RestoDetailDialogFragment();
        restoDetailDialogFragment.show(getActivity().getSupportFragmentManager(), "MODAL");
    }

    // --------------------
    // GET LOCATION USER
    // --------------------

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION_PERMS)
    protected void fetchLastKnowLocation(){
        if(! EasyPermissions.hasPermissions(getActivity(), PERMS)){
            EasyPermissions.requestPermissions(
                    this, getString(R.string.need_permission_message), RC_LOCATION_PERMS, PERMS);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        locationUser = new LatLng(location.getLatitude(), location.getLongitude());
                        this.setupLocation();
                    } else {
                        viewModel.noLocationAvailable();
                    }
                });
    }

    // --------------------
    // PERMISSIONS
    // --------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }



    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == RC_LOCATION_PERMS){
            fetchLastKnowLocation();
        }

    }


    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
