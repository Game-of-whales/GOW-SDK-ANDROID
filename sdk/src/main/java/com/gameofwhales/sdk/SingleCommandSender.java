package com.gameofwhales.sdk;

import android.content.Context;
import android.util.Log;

import com.gameofwhales.sdk.protocol.commands.Command;
import com.gameofwhales.sdk.util.DataStorage;
import com.gameofwhales.sdk.util.InternalResponseListener;
import com.gameofwhales.sdk.util.RequestBuilder;
import com.gameofwhales.sdk.util.net.HTTP;

import org.json.JSONException;

public class SingleCommandSender {
    private static String TAG = "SingleCommandSender";

    private final HTTP http;
    private final DataStorage data;
    private InternalResponseListener listener;


    public SingleCommandSender(Context context, Command command, InternalResponseListener listener)
    {
        this.listener = listener;
        http = new HTTP();
        data = new DataStorage(context, null);

        if (!data.isReady())
        {
            Log.e(TAG, "data is not ready");
            if (listener != null)
            {
                listener.OnResponse(command, true, null);
            }
            return;
        }

        try {
            RequestBuilder.create(http, data, command, listener);
        } catch (JSONException e) {
            L.e(TAG, "Error on request create");
            e.printStackTrace();

            if (listener != null)
            {
                listener.OnResponse(command, true, null);
            }

        }
    }
}
