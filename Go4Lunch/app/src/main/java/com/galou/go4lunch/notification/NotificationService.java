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
import androidx.core.app.NotificationManagerCompat;

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
    private User currentUser;
    private String currentUserId;
    private List<User> users;
    private Context context;

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("message", "received");
        context = getApplicationContext();
        if(remoteMessage.getNotification() != null) {
            this.configureRepositories();
        }

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
                && saveDataRepository.getUserId() != null){
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
            usersJoining = TextUtil.convertListToString(usersName);
            showNotification();
        }
    }

    // -------------------
    // SHOW NOTIFICATION
    // -------------------

    private void showNotification(){
        Intent intent = new Intent(this, AuthenticationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.notificcationChannel);
        String message;
        String messageBody;
        if(usersJoining != null) {
            message = context.getString(R.string.notification_message);
            messageBody = String.format(message, restaurantName, usersJoining);
        } else {
            message = context.getString(R.string.message_notification_alone);
            messageBody = String.format(message, restaurantName);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.go4lunch_icon)
                .setContentTitle(context.getString(R.string.title_notification))
                .setContentText("You have a lunch setup for today")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());

    }
}
