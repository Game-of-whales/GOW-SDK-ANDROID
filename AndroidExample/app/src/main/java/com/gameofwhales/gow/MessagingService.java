package com.gameofwhales.gow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.gameofwhales.sdk.GameOfWhales;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "GOW.Test.Messaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Payload:" + remoteMessage.getData());

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        final String pushId = remoteMessage.getData().get(GameOfWhales.PUSH_ID);

        if (notification != null)
        {
            String message = notification.getBody();
            String title = "Notification";
            showDialog(getApplicationContext(), pushId, title, message);

        } else
        {
            Log.w(TAG, "Notification not found");
        }

    }

    private void showDialog(final Context context, final String pushID, final String title, final String message)
    {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setNegativeButton("Ok!",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        GameOfWhales.PushReacted(pushID);
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//Using for example purposes
                alert.show();
            }
        });
    }




    /*private void sendNotification(String messageBody) {
        Intent intent = new Intent("com.gameofwhales.notification");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 / * Request code * /, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(com.gameofwhales.sdk.R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0 / * ID of notification * /, notificationBuilder.build());
    }*/
}
