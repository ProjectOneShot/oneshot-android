package application.oneshot.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import application.oneshot.MailboxActivity;
import application.oneshot.R;
import application.oneshot.constants.NotificationChannels;

public class FirebaseMessagingServiceExtension
        extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            notify(remoteMessage.getNotification()
                    .getBody());
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    private void notify(String messageBody) {
        final Intent intent = new Intent(this, MailboxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                NotificationChannels.NEW_MESSAGE).setSmallIcon(R.drawable.ic_oneshot_black_24dp)
                .setContentTitle(getString(R.string.notification_message_title))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
