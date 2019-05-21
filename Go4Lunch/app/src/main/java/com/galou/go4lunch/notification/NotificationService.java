package com.galou.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.galou.go4lunch.R;
import com.galou.go4lunch.authentication.AuthenticationActivity;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.repositories.SaveDataRepository;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.TextUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galou on 2019-05-20
 */
public class NotificationService extends FirebaseMessagingService {

    private SaveDataRepository saveDataRepository;
    private UserRepository userRepository;
    private String restaurantName;
    private String usersJoining;
    private String message;

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("message", "received");
        if(remoteMessage.getNotification() != null) {
            this.configureRepositories();
        }

    }

    private void configureRepositories(){
        userRepository = UserRepository.getInstance();
        saveDataRepository = SaveDataRepository.getInstance();
        saveDataRepository.configureContext(getApplicationContext());
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
                        if(user != null){
                            String restaurantUid = user.getRestaurantUid();
                            if(restaurantUid.equals(restaurantId)){
                                usersName.add(user.getUsername());
                            }

                        }
                    }
                    usersJoining = TextUtil.convertListToString(usersName);
                    sendVisualNotification();
                });

    }

    private void sendVisualNotification() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        message = "You are eating at %s with %s";
        String messageBody = String.format(message, restaurantName, usersJoining);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.title_notification));
        inboxStyle.addLine(messageBody);

        String channelId = getString(R.string.notificcationChannel);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.go4lunch_icon)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.title_notification))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

}
