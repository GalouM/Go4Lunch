package com.galou.go4lunch.map;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.ButtonActionListener;
import com.galou.go4lunch.databinding.FragmentMapViewBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private GoogleMap googleMap;
    private MapView mapView;
    private CameraUpdate cameraInitialPosition;

    private MapViewViewModel viewModel;
    private FragmentMapViewBinding binding;

    // FOR GPS PERMISSION
    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_LOCATION_PERMS = 100;

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
        this.displayLocationUser();
        if(cameraInitialPosition != null){
            this.googleMap.moveCamera(cameraInitialPosition);
        } else {
            this.centerCameraOnGPSLocation();
        }

        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
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

    private void createViewModelConnections() {
        ButtonActionListener buttonActionListener = getButtonActionListener();
        binding.setListener(buttonActionListener);

    }

    private MapViewViewModel obtainViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(MapViewViewModel.class);
    }

    //----- LISTENER BUTTON -----
    private ButtonActionListener getButtonActionListener() {
        return view -> {
            int id = view.getId();
            switch (id) {
                case R.id.location_button:
                    centerCameraOnGPSLocation();
            }
        };
    }

    // --------------------
    // MAP ACTIONS
    // --------------------
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION_PERMS)
    private void displayLocationUser(){
        if(! EasyPermissions.hasPermissions(getActivity(), PERMS)){
            EasyPermissions.requestPermissions(
                    this, getString(R.string.need_permission_message), RC_LOCATION_PERMS, PERMS);
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    @AfterPermissionGranted(RC_LOCATION_PERMS)
    private void centerCameraOnGPSLocation(){
        if(! EasyPermissions.hasPermissions(getActivity(), PERMS)){
            EasyPermissions.requestPermissions(
                    this, getString(R.string.need_permission_message), RC_LOCATION_PERMS, PERMS);
            return;
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationUser()));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_USER_LOCATION_VALUE));
    }

    private LatLng getLocationUser(){
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission")
        Location currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return (new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
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
            centerCameraOnGPSLocation();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
