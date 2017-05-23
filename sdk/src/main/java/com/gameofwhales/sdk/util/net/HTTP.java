package com.gameofwhales.sdk.util.net;


import com.gameofwhales.sdk.L;

import org.json.JSONObject;

import java.util.HashMap;

public class HTTP implements HTTPListener {
    public static final String TAG = "GOW.HTTP";

    HashMap<HTTPAsyncTask, HTTPListener> tasks = new HashMap<>();


    public void send(final String url, final JSONObject data, HTTPListener listener)
    {
        send(url, data.toString(), listener);
    }



    public void send(final String url, final String data, HTTPListener listener)
    {
        HTTPAsyncTask task = new HTTPAsyncTask(url, data, this);
        tasks.put(task, listener);
        task.execute();
    }


    @Override
    public void OnResponse(HTTPAsyncTask task, boolean error, String data) {

        HTTPListener listener = tasks.get(task);
        if (listener != null)
        {
            listener.OnResponse(task, error, data);
        }

        tasks.remove(task);
        L.i(TAG, "OnResponse[" + tasks.size() +"]: error: " + error + "  data: " + data);
    }
}
