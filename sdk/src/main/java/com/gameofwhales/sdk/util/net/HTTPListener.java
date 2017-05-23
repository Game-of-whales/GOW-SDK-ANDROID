package com.gameofwhales.sdk.util.net;


public interface HTTPListener {

    void OnResponse(HTTPAsyncTask task, boolean error, String data);
}
