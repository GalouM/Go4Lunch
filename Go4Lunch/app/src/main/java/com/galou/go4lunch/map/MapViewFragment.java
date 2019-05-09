package com.galou.go4lunch.map;


import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.galou.go4lunch.R;
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
    private FloatingActionButton floatButton;
    private CameraUpdate cameraInitialPosition;

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
        this.configureMapView(savedInstanceState, view);

        floatButton = view.findViewById(R.id.location_button);
        floatButton.setOnClickListener(view1 -> centerCameraOnGPSLocation());

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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

    private void configureMapView(Bundle savedInstanceState, View view) {
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if(savedInstanceState != null) {
            cameraInitialPosition = CameraUpdateFactory.newCameraPosition(
                    (CameraPosition) (savedInstanceState.getParcelable(STATE_KEY_MAP_CAMERA)));
        }
    }

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
        Location currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return (new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
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
