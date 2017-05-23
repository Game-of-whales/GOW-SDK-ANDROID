package com.gameofwhales.sdk.async;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class NotificationTask extends AsyncTask<Void, Void, Void> {

    private static String TAG = "GOW.NotificationTask";
    Bundle extras;
    Context context;
    Bitmap bigImage = null;
    NotificationTaskListener listener;

    public NotificationTask(Context context, Bundle extras, NotificationTaskListener listener)
    {
        this.extras = extras;
        this.listener = listener;
        this.context = context;
    }

    public void show()
    {
        String subtitle = extras.getString("subtitle");
        String title = extras.getString("title");
        String smallIcon = extras.getString("smallIcon");
        String gowImage = extras.getString("gow_img");
        String tickerText = extras.getString("tickerText");
        String collapseKey = extras.getString("collapse_key");

        String packageName = context.getPackageName();
        Intent notificationIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        notificationIntent.putExtras(extras);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (gowImage != null && !gowImage.isEmpty())
        {
            fetchBitmap(gowImage);
        }

        int iconID = 0;

        if (smallIcon != null && !smallIcon.isEmpty())
        {
            iconID = context.getResources().getIdentifier(smallIcon , "drawable", context.getPackageName());
        }

        if (iconID == 0)
        {
            try {
                final PackageManager pm = context.getPackageManager();
                final ApplicationInfo applicationInfo;
                applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                final int appIconResId=applicationInfo.icon;
                iconID = appIconResId;

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSmallIcon(iconID)
                .setTicker(tickerText)
                .setContentTitle(title)
                .setContentText(subtitle);

        Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(notificationSound);
        builder.setVibrate(new long[] {500,1000});

        if (bigImage != null)
        {
            NotificationCompat.BigPictureStyle notificationStyle = new NotificationCompat.BigPictureStyle();
            notificationStyle.setSummaryText(subtitle);
            notificationStyle.setBigContentTitle(title);
            notificationStyle.bigPicture(bigImage);
            builder.setStyle(notificationStyle);
        }

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(UUID.randomUUID().hashCode(), notification);
    }

    @Override
    protected Void doInBackground(Void... params) {

        show();
        return null;
    }

    @Override
    protected void onPostExecute(Void param)
    {
        if (listener != null)
        {
            listener.onCompleted();
        }
    }


    private boolean fetchBitmap(final String urlString)
    {
        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = null;
            if (urlString.toLowerCase().startsWith("https")) {
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bigImage = BitmapFactory.decodeStream(input);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap from: " + urlString);
            e.printStackTrace();
        }

        return false;
    }
}
