package com.galou.go4lunch.restoDetails;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.ApiDetailResponse;
import com.galou.go4lunch.models.Restaurant;
import com.galou.go4lunch.models.ResultApiPlace;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.RestaurantRepository;
import com.galou.go4lunch.repositories.SaveDataRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.OpeningHoursUtil;
import com.galou.go4lunch.util.RatingUtil;
import com.galou.go4lunch.util.SuccessOrign;
import com.galou.go4lunch.util.RetryAction;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.galou.go4lunch.util.RetryAction.GET_RESTAURANT_DETAIL;
import static com.galou.go4lunch.util.RetryAction.UPDATE_LIKED_RESTAURANT;
import static com.galou.go4lunch.util.SuccessOrign.REMOVE_RESTAURANT_LIKED;
import static com.galou.go4lunch.util.SuccessOrign.REMOVE_RESTAURANT_PICKED;
import static com.galou.go4lunch.util.SuccessOrign.UPDATE_RESTAURANT_LIKED;
import static com.galou.go4lunch.util.SuccessOrign.UPDATE_RESTAURANT_PICKED;
import static com.galou.go4lunch.util.RetryAction.UPDATE_PICKED_RESTAURANT;

/**
 * Created by galou on 2019-05-14
 */
public class RestaurantDetailViewModel extends BaseViewModel {

    private RestaurantRepository restaurantRepository;
    private SaveDataRepository saveDataRepository;
    private Restaurant restaurant;
    private Disposable disposable;

    public MutableLiveData<String> nameRestaurant = new MutableLiveData<>();
    public MutableLiveData<String> addressRestaurant = new MutableLiveData<>();
    public MutableLiveData<String> urlPhoto = new MutableLiveData<>();
    public MutableLiveData<Boolean> websiteAvailable = new MutableLiveData<>();
    public MutableLiveData<Boolean> phoneAvailable = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRestaurantLiked = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRestaurantPicked = new MutableLiveData<>();
    public MutableLiveData<Integer> rating = new MutableLiveData<>();

    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> webSite = new MutableLiveData<>();

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public LiveData<String> getWebSite() {
        return webSite;
    }

    public RestaurantDetailViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository, SaveDataRepository saveDataRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.saveDataRepository = saveDataRepository;
        user = userRepository.getUser();
    }

    public void configureSaveDataRepo(Context context){
        if(saveDataRepository.getPreferences() == null){
            saveDataRepository.configureContext(context);
        }
    }

    public void fetchInfoRestaurant(){
        isLoading.setValue(true);
        String uidSelection = restaurantRepository.getRestaurantSelected();
        boolean isRestaurantStored = false;
        if(restaurantRepository.getRestaurantsLoaded() != null) {
            for (Restaurant restaurant : restaurantRepository.getRestaurantsLoaded()) {
                if (restaurant.getUid().equals(uidSelection)) {
                    this.restaurant = restaurant;
                    isRestaurantStored = true;
                    break;
                }
            }
        }
        if(isRestaurantStored){
            configureInfoRestaurant();

        } else{
            fetchDetailFromApi(uidSelection);
        }
    }

    public void destroyDisposable(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    private void configureInfoRestaurant(){
        nameRestaurant.setValue(restaurant.getName());
        addressRestaurant.setValue(restaurant.getAddress());
        urlPhoto.setValue(restaurant.getUrlPhoto());
        rating.setValue(restaurant.getRating());
        users.setValue(restaurant.getUsersEatingHere());
        websiteAvailable.setValue(restaurant.getWebSite() != null);
        phoneAvailable.setValue(restaurant.getPhoneNumber() != null);
        isRestaurantLiked.setValue(checkIfRestaurantIsLiked());
        if(user.getRestaurantUid() != null) {
            isRestaurantPicked.setValue(user.getRestaurantUid().equals(restaurant.getUid()));
        } else {
            isRestaurantPicked.setValue(false);
        }
        isLoading.setValue(false);

    }

    private void fetchDetailFromApi(String uidSelection){
        this.disposable = restaurantRepository.streamFetchRestaurantDetails(uidSelection).subscribeWith(getObserverRestaurantDetail());
    }

    private void createRestaurant(ApiDetailResponse response){
        ResultApiPlace result = response.getResult();
        if(result != null) {
            String uid = result.getId();
            String name = result.getName();
            Double latitude = result.getGeometry().getLocation().getLat();
            Double longitude = result.getGeometry().getLocation().getLng();
            String photo = restaurantRepository.getPhotoRestaurant(result.getPhotos().get(0).getPhotoReference());
            String address = result.getVicinity();
            int openingHours = OpeningHoursUtil.getOpeningTime(result.getOpeningHours());
            int rating = RatingUtil.calculateRating(result.getRating());
            String webSite = result.getWebsite();
            String phoneNumber = result.getPhoneNumber();
            Restaurant restaurant = new Restaurant(uid, name, latitude, longitude, address, openingHours, photo, rating, phoneNumber, webSite);
            this.restaurant = restaurant;
            configureInfoRestaurant();
        }

    }

    private DisposableObserver<ApiDetailResponse> getObserverRestaurantDetail(){
        return new DisposableObserver<ApiDetailResponse>() {
            @Override
            public void onNext(ApiDetailResponse response) {
                createRestaurant(response);
            }

            @Override
            public void onError(Throwable e) {
                snackBarWithAction.setValue(GET_RESTAURANT_DETAIL);
                isLoading.setValue(false);

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void fetchPhoneNumber(){
        phoneNumber.setValue(restaurant.getPhoneNumber());
    }

    public void fetchWebsite(){
        webSite.setValue(restaurant.getWebSite());
    }

    private Boolean checkIfRestaurantIsLiked(){
        List<String> restaurantLiked = user.getLikedRestaurants();
        if(restaurantLiked != null && restaurantLiked.size() > 0) {
            for (String uid : restaurantLiked) {
                if (uid.equals(restaurant.getUid())) return true;
            }
        }

        return false;

    }

    public void updateRestaurantLiked() {
        isLoading.setValue(true);
        if(isRestaurantLiked.getValue()){
            userRepository.removeLikedRestaurant(restaurant.getUid(), user.getUid())
                    .addOnSuccessListener(onSuccessListener(REMOVE_RESTAURANT_LIKED))
                    .addOnFailureListener(this.onFailureListener(UPDATE_LIKED_RESTAURANT));
        } else {
            userRepository.addLikedRestaurant(restaurant.getUid(), user.getUid())
                    .addOnSuccessListener(onSuccessListener(UPDATE_RESTAURANT_LIKED))
                    .addOnFailureListener(this.onFailureListener(UPDATE_LIKED_RESTAURANT));
        }

    }

    public void updatePickedRestaurant() {
        isLoading.setValue(true);
        if(isRestaurantPicked.getValue()){
            userRepository.updateRestaurantPicked(null, null, user.getUid())
                    .addOnSuccessListener(onSuccessListener(REMOVE_RESTAURANT_PICKED))
                    .addOnFailureListener(this.onFailureListener(UPDATE_PICKED_RESTAURANT));
            saveDataRepository.saveRestaurantId(null);
            saveDataRepository.saveRestaurantName(null);
        } else {
            userRepository.updateRestaurantPicked(restaurant.getUid(), restaurant.getName(), user.getUid())
                    .addOnSuccessListener(onSuccessListener(UPDATE_RESTAURANT_PICKED))
                    .addOnFailureListener(this.onFailureListener(UPDATE_PICKED_RESTAURANT));
            saveDataRepository.saveRestaurantId(restaurant.getUid());
            saveDataRepository.saveRestaurantName(restaurant.getName());
        }

    }

    private OnSuccessListener<Void> onSuccessListener(final SuccessOrign origin){
        return aVoid -> {
            switch (origin){
                case UPDATE_RESTAURANT_PICKED:
                    snackBarText.setValue(R.string.eating_here_today);
                    isRestaurantPicked.setValue(true);
                    restaurant.addUser(user);
                    fetchInfoRestaurant();
                    isLoading.setValue(false);
                    break;
                case REMOVE_RESTAURANT_PICKED:
                    snackBarText.setValue(R.string.not_eating_here);
                    isRestaurantPicked.setValue(false);
                    restaurant.removeUser(user);
                    fetchInfoRestaurant();
                    isLoading.setValue(false);
                    break;
                case UPDATE_RESTAURANT_LIKED:
                    isRestaurantLiked.setValue(true);
                    isLoading.setValue(false);
                    break;
                case REMOVE_RESTAURANT_LIKED:
                    isRestaurantLiked.setValue(false);
                    isLoading.setValue(false);
                    break;
            }

        };

    }

    @Override
    public void retry(RetryAction retryAction) {
        switch (retryAction){
            case UPDATE_PICKED_RESTAURANT:
                updatePickedRestaurant();
                break;
            case UPDATE_LIKED_RESTAURANT:
                updateRestaurantLiked();
                break;
            case GET_RESTAURANT_DETAIL:
                fetchInfoRestaurant();
                break;
        }

    }


}
