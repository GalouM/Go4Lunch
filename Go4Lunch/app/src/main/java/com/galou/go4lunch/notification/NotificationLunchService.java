package com.galou.go4lunch.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.galou.go4lunch.R;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.SaveDataRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.TextUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String restaurantAddress;
    private String usersJoining;
    private Context context;
    private User currentUser;
    private String currentUserId;
    private List<User> users;

    private final int NOTIFICATION_ID = 007;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.configureRepositories();

    }

    // -------------------
    // CONFIGURE DATA
    // -------------------

    private void configureRepositories(){
        userRepository = UserRepository.getInstance();
        saveDataRepository = SaveDataRepository.getInstance();
        saveDataRepository.configureContext(context);
        currentUserId = saveDataRepository.getUserId();
        if(saveDataRepository.getNotificationSettings(currentUserId)
                && getCurrentUser() != null){
            this.fetchUsers();
        }

    }

    private void fetchUsers() {
        users = new ArrayList<>();
        userRepository.getAllUsersFromFirebase()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        User user = documentSnapshot.toObject(User.class);
                        if(user != null && user.getRestaurantUid() != null) {
                            if (user.getUid().equals(currentUserId)) {
                                currentUser = user;
                            } else {
                                users.add(user);
                            }
                        }
                    }
                    fetchUsersGoing();
                });

    }

    private void fetchUsersGoing(){
        if(currentUser != null) {
            List<String> usersName = new ArrayList<>();
            for (User user : users) {
                if (user.getRestaurantUid().equals(currentUser.getRestaurantUid())){
                    usersName.add(user.getUsername());
                }
            }
            restaurantName = currentUser.getRestaurantName();
            restaurantAddress = currentUser.getRestaurantAddress();
            usersJoining = TextUtil.convertListToString(usersName);
            showNotification();
        }
    }

    @Nullable
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    // -------------------
    // SHOW NOTIFICATION
    // -------------------

    private void showNotification(){
        String channelId = context.getString(R.string.notificcationChannel);
        String message;
        String messageBody;
        if(usersJoining != null && usersJoining.length() > 0) {
            message = context.getString(R.string.notification_message);
            messageBody = String.format(message, restaurantName, usersJoining, restaurantAddress);
        } else {
            message = context.getString(R.string.message_notification_alone);
            messageBody = String.format(message, restaurantName, restaurantAddress);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.go4lunch_icon)
                .setContentTitle(context.getString(R.string.title_notification))
                .setContentText(context.getString(R.string.subtitle_notification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }
}