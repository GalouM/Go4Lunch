package com.galou.go4lunch.main;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.firebase.ui.auth.AuthUI;
import com.galou.go4lunch.R;
import com.galou.go4lunch.authentication.AuthenticationActivity;
import com.galou.go4lunch.chat.ChatFragment;
import com.galou.go4lunch.databinding.ActivityMainBinding;
import com.galou.go4lunch.databinding.MainActivityNavHeaderBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.restaurantsList.ListViewFragment;
import com.galou.go4lunch.restaurantsList.MapViewFragment;
import com.galou.go4lunch.restoDetails.RestoDetailDialogFragment;
import com.galou.go4lunch.settings.SettingsActivity;
import com.galou.go4lunch.util.RetryAction;
import com.galou.go4lunch.util.SnackBarUtil;
import com.galou.go4lunch.workmates.WorkmatesFragment;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainActivityContract {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;

    private PendingIntent pendingIntent;

    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureBindingAndViewModel();
        this.configureUI();
        this.createViewModelConnections();

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.configureInfoUser();
        viewModel.closeSettings();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
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
    }

    private void setupSnackBar(){
        View view = findViewById(android.R.id.content);
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(view, getString(message));
            }
        });

    }

    private void setupLogoutRequest(){
        viewModel.getLogout().observe(this, logout -> logoutUser());
    }

    private void setupSettingsRequest(){
        viewModel.getSettings().observe(this, this::settings);
    }

    private void setupOpenDetailRestaurant(){
        viewModel.getOpenDetailRestaurant().observe(this, open -> displayRestaurantDetail());
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
        if(id == R.id.menu_main_activity_search){
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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
                viewModel.updateRestaurantToDisplay();
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
            case R.id.action_chat:
                ChatFragment chatView = new ChatFragment();
                replaceAndShowFragment(chatView);
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
                    viewModel.decrementIdleResource();
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(viewModel.onFailureListener(RetryAction.LOGOUT));

    }

    @Override
    public void settings(Boolean setting) {
        if(setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void displayRestaurantDetail(){
        RestoDetailDialogFragment restoDetailDialogFragment = new RestoDetailDialogFragment();
        restoDetailDialogFragment.show(getSupportFragmentManager(), "MODAL");
    }

    // --------------------
    // TESTING
    // --------------------
    @VisibleForTesting
    public CountingIdlingResource getEspressoIdlingResourceForMainActivity() {
        return viewModel.getEspressoIdlingResource();
    }
}

