package com.gameofwhales.sdk.util;

import com.gameofwhales.sdk.L;
import com.gameofwhales.sdk.protocol.commands.Command;
import com.gameofwhales.sdk.util.net.HTTPAsyncTask;
import com.gameofwhales.sdk.util.net.HTTPListener;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class InternalResponseListener implements HTTPListener {

    private Command command;
    private final static String TAG = "GOW.IRL";

    public InternalResponseListener(Command command)
    {
        this.command = command;
    }


    @Override
    public void OnResponse(HTTPAsyncTask task, boolean error, String data)
    {
        JSONObject object = null;
        try {
             object = new JSONObject(data);
        } catch (JSONException e) {
            L.e(TAG, "Json parse error");
            e.printStackTrace();
        }

        OnResponse(command, error, object);
    }

    public abstract void OnResponse(Command command, boolean error, JSONObject response);
}
