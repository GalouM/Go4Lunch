package com.galou.go4lunch.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.galou.go4lunch.R;
import com.galou.go4lunch.authentication.AuthenticationActivity;
import com.galou.go4lunch.databinding.ActivityMainBinding;
import com.galou.go4lunch.databinding.MainActivityNavHeaderBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.notification.EraseRestaurantInfo;
import com.galou.go4lunch.notification.NotificationLunchService;
import com.galou.go4lunch.restaurantsList.ListViewFragment;
import com.galou.go4lunch.restaurantsList.MapViewFragment;
import com.galou.go4lunch.restoDetails.RestoDetailDialogFragment;
import com.galou.go4lunch.settings.SettingsActivity;
import com.galou.go4lunch.util.PositionUtil;
import com.galou.go4lunch.util.RetryAction;
import com.galou.go4lunch.util.SnackBarUtil;
import com.galou.go4lunch.workmates.WorkmatesFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityContract, EasyPermissions.PermissionCallbacks {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;

    private PendingIntent pendingIntentAlarm;
    private PendingIntent pendingIntentReset;

    private int AUTOCOMPLETE_REQUEST_CODE = 12345;
    private List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
    private Intent autoCompleteIntent;

    private static int[] TIME_NOTIFICATION = {12, 0};
    private static int[] TIME_RESET = {23, 59};

    // FOR GPS PERMISSION
    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_LOCATION_PERMS = 100;
    private FusedLocationProviderClient fusedLocationClient;


    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.configureBindingAndViewModel();
        this.configureUI();
        this.createViewModelConnections();
        this.configureResetData();
        this.createNotificationChannel();
        this.fetchLastKnowLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.configureInfoUser();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            this.onAutocompleteRequest(resultCode, data);
        }
    }


    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------

    private void configureBindingAndViewModel(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        viewModel.configureSaveDataRepo(getApplicationContext());
    }

    private MainActivityViewModel obtainViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(MainActivityViewModel.class);
    }

    private void createViewModelConnections() {
        setupSnackBar();
        setupLogoutRequest();
        setupSettingsRequest();
        setupOpenDetailRestaurant();
        setupNotification();
        setupLocationAutocomplete();
    }

    private void setupSnackBar(){
        View view = findViewById(android.R.id.content);
        viewModel.getSnackBarMessage().observe(this, messageEvent -> {
            Integer message = messageEvent.getContentIfNotHandle();
            if (message != null) {
                SnackBarUtil.showSnackBar(view, getString(message));
            }

        });

    }

    private void setupLogoutRequest(){
        viewModel.getLogout().observe(this, logOutEvent -> {
            if(logOutEvent.getContentIfNotHandle() != null){
                this.logoutUser();
            }
        });
    }

    private void setupSettingsRequest(){
        viewModel.getSettings().observe(this, settingEvent -> {
            if(settingEvent.getContentIfNotHandle() != null){
                this.settings();
            }
        });
    }

    private void setupOpenDetailRestaurant(){
        viewModel.getOpenDetailRestaurant().observe(this, detailEvent -> {
            if (detailEvent.getContentIfNotHandle() != null) {
                displayRestaurantDetail();
            }
        });
    }

    private void setupNotification(){
        viewModel.getIsNotificationEnable().observe(this, this::configureNotification);
    }

    private void setupLocationAutocomplete(){
        viewModel.getLocation().observe(this, this::configureAutocomplete);
    }

    // --------------------
    // CONFIGURE NAV UI
    // --------------------

    private void configureUI(){
        this.configureToolbar();
        this.configureBottomView();
        this.configureAndShowFirstFragment();
        this.configureDrawerLayout();
        this.configureNavigationView();

    }

    //----- INITIAL STATE -----

    private void configureAndShowFirstFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if(fragment == null) {
            fragment = (MapViewFragment) new MapViewFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment)
                    .commit();
        }

    }

    //----- TOOLBAR -----

    private void configureToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id  = item.getItemId();
        if(id == R.id.menu_main_activity_search && autoCompleteIntent != null){
            startActivityForResult(autoCompleteIntent, AUTOCOMPLETE_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    //----- NAV DRAWER -----
    private void configureDrawerLayout(){
        drawerLayout = binding.drawerView;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void configureNavigationView(){
        navigationView = binding.navigationView;
        MainActivityNavHeaderBinding navHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.main_activity_nav_header, navigationView, false);
        navHeaderBinding.setViewmodel(viewModel);
        navHeaderBinding.setLifecycleOwner(this);
        binding.navigationView.addHeaderView(navHeaderBinding.getRoot());
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)){
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        item.setChecked(false);
        switch (id){
            case R.id.main_activity_drawer_settings:
                viewModel.openSettings();
                break;
            case R.id.main_activity_drawer_logout:
                viewModel.logoutUserFromApp();
                break;
            case R.id.main_activity_drawer_lunch:
                viewModel.showUserRestaurant();
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    //----- BOTTOM NAV -----
    private void configureBottomView() {
        bottomNavigationView = binding.bottomNav;
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
    }

    private boolean updateMainFragment(int itemId) {
        switch (itemId){
            case R.id.action_map:
               MapViewFragment mapView = new MapViewFragment();
               replaceAndShowFragment(mapView);
                break;
            case R.id.action_list:
                ListViewFragment listView = new ListViewFragment();
                replaceAndShowFragment(listView);
                break;
            case R.id.action_workmates:
                WorkmatesFragment workmatesView = new WorkmatesFragment();
                replaceAndShowFragment(workmatesView);
                break;
        }

        return true;
    }

    private void replaceAndShowFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }


    // --------------------
    // ACTIONS FROM VIEW MODEL
    // --------------------


    @Override
    public void logoutUser() {
        AuthUI.getInstance().signOut(this)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(viewModel.onFailureListener(RetryAction.LOGOUT));

    }

    @Override
    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    @Override
    public void displayRestaurantDetail(){
        RestoDetailDialogFragment restoDetailDialogFragment = new RestoDetailDialogFragment();
        restoDetailDialogFragment.show(getSupportFragmentManager(), "MODAL");
    }

    @Override
    public void configureNotification(boolean isEnable) {
        configureNotificationIntent();
        if(isEnable) enableNotifications();
        if(!isEnable) disableNotification();

    }

    @Override
    public void configureAutocomplete(LatLng position){
        LatLngBounds bounds = PositionUtil.convertToBounds(position, 2500);
        autoCompleteIntent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setLocationRestriction(RectangularBounds.newInstance(bounds.southwest, bounds.northeast))
                .build(this);

    }

    // -----------------
    // AUTOCOMPLETE CLICK
    // -----------------
    private void onAutocompleteRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            boolean isRestaurant = false;
            Place place = Autocomplete.getPlaceFromIntent(data);
            if(place.getTypes() != null) {

                for (Place.Type type : place.getTypes()) {
                    if (type == Place.Type.RESTAURANT) {
                        isRestaurant = true;
                        break;
                    }
                }
            }
            if(isRestaurant || place.getTypes() == null) {
                viewModel.showRestaurantSelected(place.getId());
            }
        }
    }

    // -----------------
    // NOTIFICATION
    // -----------------

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = getString(R.string.notificcationChannel);
            CharSequence name = getString(R.string.name_channel);
            String description = getString(R.string.description_channel);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void configureNotificationIntent(){
        Intent notificationIntent = new Intent(this, NotificationLunchService.class);
        pendingIntentAlarm = PendingIntent.getBroadcast(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private void enableNotifications() {
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.HOUR_OF_DAY, TIME_NOTIFICATION[0]);
        notificationTime.set(Calendar.MINUTE, TIME_NOTIFICATION[1]);
        notificationTime.set(Calendar.SECOND, 0);

        Calendar calendar = Calendar.getInstance();
        if(notificationTime.before(calendar)){
            notificationTime.add(Calendar.DATE, 1);
        }

        ComponentName receiver = new ComponentName(getApplicationContext(), NotificationLunchService.class);

        PackageManager pm = getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentAlarm);


    }

    private void disableNotification() {
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntentAlarm);

        ComponentName receiver = new ComponentName(getApplicationContext(), NotificationLunchService.class);
        PackageManager pm = getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

    // --------------------
    // GET LOCATION USER
    // --------------------

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION_PERMS)
    private void fetchLastKnowLocation(){
        if(! EasyPermissions.hasPermissions(this, PERMS)){
            EasyPermissions.requestPermissions(
                    this, getString(R.string.need_permission_message), RC_LOCATION_PERMS, PERMS);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng locationUser = new LatLng(location.getLatitude(), location.getLongitude());
                        viewModel.configureLocationUser(locationUser);
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


    // --------------------
    // RESET DATA RESTAURANT EVERY DAY
    // --------------------

    /*
    this shouldn't be done from the application but from a central server instead
     */
    private void configureResetData(){
        Intent notificationIntent = new Intent(this, EraseRestaurantInfo.class);
        pendingIntentReset = PendingIntent.getBroadcast(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar resetTime = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        resetTime.setTimeInMillis(System.currentTimeMillis());
        resetTime.set(Calendar.HOUR_OF_DAY, TIME_RESET[0]);
        resetTime.set(Calendar.MINUTE, TIME_RESET[1]);
        resetTime.set(Calendar.SECOND, 0);

        if(resetTime.before(calendar)){
            resetTime.add(Calendar.DATE, 1);
        }

        ComponentName receiver = new ComponentName(getApplicationContext(), EraseRestaurantInfo.class);
        PackageManager pm = getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC, resetTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentReset);

    }
}

