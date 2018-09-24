package cabiso.daphny.com.g_companion.notifications;

/**
 * Created by cicctuser on 8/22/2018.
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cabiso.daphny.com.g_companion.BuyingProcess.ForMeetUpActivity;
import cabiso.daphny.com.g_companion.BuyingProcess.Pending_Activity;
import cabiso.daphny.com.g_companion.BuyingProcess.Sold_Activity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.MainDIYS.ViewMoreDiysBidActivity;
import cabiso.daphny.com.g_companion.MyProfileActivity;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Acer on 17/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        // Create an explicit intent for an Activity in your app

        if(remoteMessage.getNotification().getTitle().equals("Notification")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }
        else if (remoteMessage.getNotification().getTitle().equals("Profile Notification")){
            Intent intent = new Intent(this, MyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("Buyer Notification")){
            Intent intent = new Intent(this, Pending_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }
        else if (remoteMessage.getNotification().getTitle().equals("Bidding Notification")){
            Intent intent = new Intent(this, ViewMoreDiysBidActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }
        else if (remoteMessage.getNotification().getTitle().equals("Bidders Notification")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("Pending Item Notification")){
            Intent intent = new Intent(this, ForMeetUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("Completed")){
            Intent intent = new Intent(this, Sold_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("Rate")){
            Intent intent = new Intent(this, Sold_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("Rate")){
            Intent intent = new Intent(this, Sold_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("Report Notification")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("Warning!")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }

        else if (remoteMessage.getNotification().getTitle().equals("On Sale Notification")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }
        else {

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.new_logo_green1)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(defaultSoundUri2)
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setVibrate(new long[android.app.Notification.DEFAULT_VIBRATE])
                    .setCategory(android.app.Notification.CATEGORY_CALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }


    }

}
