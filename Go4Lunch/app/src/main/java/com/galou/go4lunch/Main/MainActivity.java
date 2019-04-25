package com.galou.go4lunch.Main;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.galou.go4lunch.List.ListViewFragment;
import com.galou.go4lunch.Map.MapViewFragment;
import com.galou.go4lunch.R;
import com.galou.go4lunch.chat.ChatFragment;
import com.galou.go4lunch.databinding.ActivityMainBinding;
import com.galou.go4lunch.databinding.MainActivityNavHeaderBinding;
import com.galou.go4lunch.util.SnackBarUtil;
import com.galou.go4lunch.workmates.WorkmatesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainActivityContract {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseUser user;
    private ActivityMainBinding binding;

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.configureToolbar();
        this.configureBottomView();
        this.configureAndShowFirstFragment();
        this.configureDrawerLayout();
        this.configureNavigationView();
        viewModel = obtainViewModel();
        setupSnackBar();
        setupLogoutRequest();

        viewModel.setLoggedUser();
        setupLoggedUser();



    }

    private MainActivityViewModel obtainViewModel() {
        return ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);
    }

    private void configureToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

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


    private void configureAndShowFirstFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if(fragment == null) {
            fragment = (MapViewFragment) new MapViewFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment)
                    .commit();
        }

    }


    private void replaceAndShowFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.main_activity_drawer_logout:
                viewModel.logoutUserFromApp();
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_main_activity_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if(searchItem != null){
            searchView = (SearchView) searchItem.getActionView();
        }
        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }

        return true;

    }



    private void setupSnackBar(){
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(getWindow().getDecorView().getRootView(), getString(message));
            }
        });

    }


    private void setupLogoutRequest(){
        viewModel.getLogout().observe(this, logout -> logoutUser());
    }

    private void setupLoggedUser(){
        viewModel.getLoggedUser().observe(this, userLogged -> user = userLogged);
        //Log.e("tag", this.user.getDisplayName());
    }


    @Override
    public void logoutUser() {
        AuthUI.getInstance()
                .signOut(this);
        this.finish();

    }



}

