package com.galou.go4lunch.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.galou.go4lunch.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bottom_nav) BottomNavigationView bottomNavigationView;
    @BindView(R.id.main_activity_drawer) DrawerLayout drawerLayout;
    @BindView(R.id.main_activity_nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.configureBottomView();
        this.configureAndShowFirstFragment();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);

    }

    private void configureDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void configureNavigationView(){
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
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
