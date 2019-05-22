package com.galou.go4lunch.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.SaveDataRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.TextUtil;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galou on 2019-05-22
 */
public class NotificationLunchService extends BroadcastReceiver {

    private SaveDataRepository saveDataRepository;
    private UserRepository userRepository;
    private String restaurantName;
    private String usersJoining;
    private Context context;
    private String channelId;

    private final int NOTIFICATION_ID = 007;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        channelId = context.getString(R.string.notificcationChannel);
        this.configureRepositories();
    }

    // -------------------
    // CONFIGURE DATA
    // -------------------

    private void configureRepositories(){
        userRepository = UserRepository.getInstance();
        saveDataRepository = SaveDataRepository.getInstance();
        saveDataRepository.configureContext(context);
        String userId = saveDataRepository.getUserId();
        if(saveDataRepository.getNotificationSettings(userId)
                && saveDataRepository.getRestaurantName() != null
                && saveDataRepository.getUserId() != null){
            this.fetchInfoRestaurant();
        }

    }

    private void fetchInfoRestaurant() {
        restaurantName = saveDataRepository.getRestaurantName();
        this.fetchNameUsersGoing(saveDataRepository.getRestaurantId());
    }

    private void fetchNameUsersGoing(String restaurantId) {
        List<String> usersName = new ArrayList<>();
        userRepository.getAllUsersFromFirebase()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        User user = documentSnapshot.toObject(User.class);
                        if(user != null && user.getRestaurantUid() != null){
                            String restaurantUid = user.getRestaurantUid();
                            if(restaurantUid.equals(restaurantId)){
                                usersName.add(user.getUsername());
                            }

                        }
                    }
                    usersJoining = TextUtil.convertListToString(usersName);
                    showNotification();
                });

    }

    // -------------------
    // SHOW NOTIFICATION
    // -------------------

    private void showNotification(){
        String channelId = context.getString(R.string.notificcationChannel);
        String message = context.getString(R.string.notification_message);
        String messageBody = String.format(message, restaurantName, usersJoining);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.go4lunch_icon)
                .setContentTitle(context.getString(R.string.title_notification))
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }
}
