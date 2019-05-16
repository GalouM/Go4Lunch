package com.galou.go4lunch.restaurantsList;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.galou.go4lunch.R;
import com.galou.go4lunch.databinding.FragmentMapViewBinding;
import com.galou.go4lunch.models.Restaurant;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.galou.go4lunch.util.PositionUtil.convertLocationForApi;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseRestaurantsListFragment implements OnMapReadyCallback,
        EasyPermissions.PermissionCallbacks, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private MapView mapView;
    private CameraUpdate cameraInitialPosition;

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
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        PlacesClient placesClient = Places.createClient(getActivity());

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
        this.setupMap();

    }

    @AfterPermissionGranted(RC_LOCATION_PERMS)
    private void setupMap(){
        if(! EasyPermissions.hasPermissions(getActivity(), PERMS)){
            EasyPermissions.requestPermissions(
                    this, getString(R.string.need_permission_message), RC_LOCATION_PERMS, PERMS);
            return;
        }
        this.displayLocationUser();
        if(cameraInitialPosition != null){
            this.googleMap.moveCamera(cameraInitialPosition);
        } else {
            this.centerCameraOnGPSLocation();
        }
        viewModel.setupLocation(convertLocationForApi(getLocationUser()));
        viewModel.requestListRestaurants();

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
    private void centerCameraOnGPSLocation(){
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationUser()));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_USER_LOCATION_VALUE));
    }

    @SuppressLint("MissingPermission")
    private LatLng getLocationUser() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return (new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
    }

    // --------------------
    // ACTION FROM VIEWMODEL
    // --------------------

    @Override
    public void displayRestaurants(List<Restaurant> restaurants){
        if(googleMap != null) {
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
                    marker.setTag(positionIndex);
                }
            }
            googleMap.setOnMarkerClickListener(this);
        }

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
            setupMap();
        }

    }


    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
