package com.galou.go4lunch.notification;

import android.app.PendingIntent;
import android.content.Intent;

import com.galou.go4lunch.authentication.AuthenticationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by galou on 2019-05-20
 */
public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            this.sendVisualNotification(message);
        }

    }

    private void sendVisualNotification(String message) {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
