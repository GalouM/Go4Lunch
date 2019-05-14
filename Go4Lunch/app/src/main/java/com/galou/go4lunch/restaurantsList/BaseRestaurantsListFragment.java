package com.galou.go4lunch.restaurantsList;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.galou.go4lunch.R;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.util.SnackBarUtil;

import java.util.List;

/**
 * Created by galou on 2019-05-09
 */
public abstract class BaseRestaurantsListFragment extends Fragment implements RestaurantsListViewContract {

    protected RestaurantsListViewModel viewModel;

    public abstract void displayRestaurants(List<Restaurant> restaurants);

    protected void createViewModelConnections() {
        this.setupRestaurantDisplay();
        this.setupSnackBarWithAction();
        this.setupSnackBar();

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
        viewModel.getSnackBarMessage().observe(this, action -> {
            if(action != null){
                SnackBarUtil.showSnackBar(getView(), getString(R.string.error_unknown_error));
            }
        });

    }
}