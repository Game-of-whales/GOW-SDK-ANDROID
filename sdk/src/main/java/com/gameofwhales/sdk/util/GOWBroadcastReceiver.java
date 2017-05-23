package com.gameofwhales.sdk.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.gameofwhales.sdk.GameOfWhales;
import com.gameofwhales.sdk.L;

import org.json.JSONException;
import org.json.JSONObject;

public class GOWBroadcastReceiver extends WakefulBroadcastReceiver {

    private static String TAG = "GOW.BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        L.i(TAG, "OnBroadcastReceive");
        Bundle bundle = intent.getExtras();

        if (L.isDebug)
        {

            JSONObject json = new JSONObject();

            try {
                for (String key : bundle.keySet()) {
                    json.put(key, bundle.get(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            L.d(TAG, json.toString());
        }

        if (bundle.containsKey(GameOfWhales.PUSH_GOW))
        {
            //Start service that will send pushDelivered command
            ComponentName comp = new ComponentName(context.getPackageName(), GOWPushService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
            if (!GameOfWhales.IsAppForeground())
                this.abortBroadcast();
        }
    }
}
