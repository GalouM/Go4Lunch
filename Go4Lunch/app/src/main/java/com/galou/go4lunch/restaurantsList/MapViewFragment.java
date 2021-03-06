package com.galou.go4lunch.restaurantsList;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.galou.go4lunch.R;
import com.galou.go4lunch.databinding.FragmentMapViewBinding;
import com.galou.go4lunch.models.Restaurant;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseRestaurantsListFragment implements OnMapReadyCallback,
        EasyPermissions.PermissionCallbacks, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private MapView mapView;
    private CameraUpdate cameraInitialPosition;

    private FragmentMapViewBinding binding;

    private static float ZOOM_USER_LOCATION_VALUE = 15;

    // FOR MAP STATE WHEN ROTATE
    private static String STATE_KEY_MAP_CAMERA = "keyMap";


    public MapViewFragment() {}

    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        this.configureBindingAndViewModel(view);
        this.createViewModelConnections();
        this.configureMapView(savedInstanceState);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(googleMap != null){
            outState.putParcelable(STATE_KEY_MAP_CAMERA, googleMap.getCameraPosition());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // --------------------
    // CONFIGURE UI
    // --------------------

    private void configureMapView(Bundle savedInstanceState) {
        mapView = (MapView) binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if(savedInstanceState != null) {
            cameraInitialPosition = CameraUpdateFactory.newCameraPosition(
                    (CameraPosition) (savedInstanceState.getParcelable(STATE_KEY_MAP_CAMERA)));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        viewModel.checkIfLocationIsAvailable();

    }



    @Override
    public void configureLocation(LatLng location){
        if(googleMap != null) {
            this.displayLocationUser();
            if (cameraInitialPosition != null) {
                this.googleMap.moveCamera(cameraInitialPosition);
            } else {
                this.centerCameraOnGPSLocation(location);
            }
        }

    }

    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------
    private void configureBindingAndViewModel(View view) {
        binding = FragmentMapViewBinding.bind(view);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        viewModel.updateRestaurantSelected(marker.getTag().toString());
        return false;
    }

    // --------------------
    // MAP ACTIONS
    // --------------------
    @SuppressLint("MissingPermission")
    private void displayLocationUser(){
        googleMap.setMyLocationEnabled(true);


    }

    @SuppressLint("MissingPermission")
    private void centerCameraOnGPSLocation(LatLng locationUser) {
        if (locationUser != null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationUser));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_USER_LOCATION_VALUE));
        }
    }

    // --------------------
    // ACTION FROM VIEWMODEL
    // --------------------

    @Override
    public void displayRestaurants(List<Restaurant> restaurants){
        if(googleMap != null) {
            googleMap.clear();
            for (Restaurant restaurant : restaurants) {
                Double latitude = restaurant.getLatitude();
                Double longitude = restaurant.getLongitude();
                LatLng positionRestaurant = new LatLng(latitude, longitude);
                if(restaurant.getUsersEatingHere().size() > 0){
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(positionRestaurant)
                            .title(restaurant.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_selected)));
                    marker.setTag(restaurant.getUid());
                } else {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(positionRestaurant)
                            .title(restaurant.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_normal)));
                    marker.setTag(restaurant.getUid());
                }
            }
            googleMap.setOnMarkerClickListener(this);
        }

    }


}
