package com.gameofwhales.sdk.util;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.gameofwhales.sdk.GameOfWhales;
import com.gameofwhales.sdk.L;
import com.gameofwhales.sdk.SingleCommandSender;
import com.gameofwhales.sdk.async.NotificationTask;
import com.gameofwhales.sdk.protocol.commands.Command;
import com.gameofwhales.sdk.protocol.commands.PushDeliveredCommand;

import org.json.JSONObject;

public class GOWPushService extends IntentService {

    private static String TAG = "GOW.PushService";
    private Intent intent;
    private SingleCommandSender sender = null;

    public GOWPushService() {
        super(TAG);
    }
    //

    @Override
    protected void onHandleIntent(Intent intent) {
        L.i(TAG, "onHandleIntent");
        this.intent = intent;
        Bundle extras = intent.getExtras();

        if (extras.containsKey(GameOfWhales.PUSH_ID))
        {
            sendPushDelivered(extras.getString(GameOfWhales.PUSH_ID));
        }

        L.i(TAG, "IsForeground: " + GameOfWhales.IsAppForeground());
        if (!GameOfWhales.IsAppForeground())
        {
            NotificationTask notification = new NotificationTask(this, extras, null);
            notification.show();
            complete();
        }
    }

    private void sendPushDelivered(final String pushID)
    {
        if (GameOfWhales.getInstance() != null)
        {
            GameOfWhales.PushDelivered(pushID);
        }
        else
        {
            Command command = new PushDeliveredCommand(pushID);

            InternalResponseListener listener = new InternalResponseListener(command) {
                @Override
                public void OnResponse(Command command, boolean error, JSONObject response) {
                    L.i(TAG, "onResponse: command:" + command.getID() + "error: " + error + ", response: " + response);
                    sender = null;
                    complete();
                }
            };

            sender = new SingleCommandSender(getApplicationContext(), command, listener);
        }
    }

    private void complete()
    {
        if (sender == null)
        {
            GOWBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

}
