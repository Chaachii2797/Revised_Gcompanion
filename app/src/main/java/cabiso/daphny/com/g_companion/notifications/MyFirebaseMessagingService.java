package cabiso.daphny.com.g_companion.notifications;

/**
 * Created by cicctuser on 8/22/2018.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by Acer on 17/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String matchId = remoteMessage.getData().get("match_id");
            String user = remoteMessage.getData().get("name");
            String userPic = remoteMessage.getData().get("picture");

            sendNotificationMatchInvite(title, message, matchId, user, userPic);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            Log.d(TAG, remoteMessage.getNotification().getTitle());
            String icon = remoteMessage.getNotification().getIcon();
            sendNotification(title, remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.chat_icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[Notification.DEFAULT_VIBRATE])
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //test rani
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//        Log.d(TAG,title);
//        Log.d(TAG,messageBody);

        Log.d(TAG, title);
        if (title.equals("Game is found")) {
            Intent intent2 = new Intent("gameReady");
            Log.d(TAG, "ni sud sa game ready");
            this.sendBroadcast(intent2);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } else if (title.equals("UpdateVenue")) {
            Intent intent3 = new Intent("updateVenue");
            intent3.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa update venue");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent3);
        } else if (title.equals("UpdateDate")) {
            Intent intent4 = new Intent("updateDate");
            intent4.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa update date");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent4);
        } else if (title.equals("All players are ready")) {
            Intent intent5 = new Intent("matchReady");
            intent5.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa match ready");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent5);
        } else if (title.equals("SomeoneLeft")) {
            Intent intent6 = new Intent("someoneLeft");
            intent6.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa someone left");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent6);
        } else if (title.equals("MessageUpdate")) {
            Intent intent6 = new Intent("MessageUpdate");
            intent6.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa match ready");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent6);
        } else if (title.equals("SomeoneLeft")) {
            Intent intent6 = new Intent("someoneLeft");
            intent6.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa someone left");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent6);
        } else if (title.equals("SomeoneJoined")) {
            Intent intent7 = new Intent("someoneJoined");
            intent7.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa someone joined");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent7);
        } else if (title.equals("Match Invite")) {
            Intent intent8 = new Intent("matchInvite");
            intent8.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa match invite");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent8);

            String[] tokens = messageBody.split(";");

            Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder2 = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.chat_icon)
                    .setContentTitle(title)
                    .setContentText(tokens[0])
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri2)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[Notification.DEFAULT_VIBRATE])
                    .setPriority(Notification.PRIORITY_MAX);

            NotificationManager notificationManager2 =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager2.notify(0 /* ID of notification */, notificationBuilder2.build());
        } else if (title.equals("Level Up")){
            Intent intent9 = new Intent("levelUp");
            intent9.putExtra("message", messageBody);
            Log.d(TAG, "ni sud sa match invite");
            Log.d(TAG, messageBody);
            this.sendBroadcast(intent9);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } else {
            //like og comment ari mo sud
            Intent intent1 = new Intent("updateNotification");
            Log.d(TAG, "ni sud sa update notifs");
            this.sendBroadcast(intent1);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    private void sendNotificationMatchInvite(String title, String messageBody, String matchId, String user, String userPic) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
        String formattedMessage = messageBody + ";" + matchId + ";" + userPic + ";" + user;
        Intent intent8 = new Intent("matchInvite");
        intent8.putExtra("message", formattedMessage);
        Log.d(TAG, "ni sud sa match invite");
        Log.d(TAG, messageBody);
        this.sendBroadcast(intent8);


        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent piDismiss = PendingIntent.getActivity(this, 0, intent, 0);

        //build notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.chat_icon)
                        .setCategory(Notification.CATEGORY_CALL)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                        .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                        .addAction(R.drawable.cast_ic_notification_0,
                                "View Match", piDismiss)
                        .addAction(R.drawable.cast_ic_notification_small_icon,
                                "Cancel", null)
                        .setFullScreenIntent(piDismiss, true);

        NotificationManager notificationManager2 =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager2.notify(0 /* ID of notification */, builder.build());

//        Uri defaultSoundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder2 = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_logo_with_square)
//                .setContentTitle(title)
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri2)
//                .setContentIntent(pendingIntent)
//                .setVibrate(new long[Notification.DEFAULT_VIBRATE])
//                .setPriority(Notification.PRIORITY_HIGH);
//
//
    }
}
