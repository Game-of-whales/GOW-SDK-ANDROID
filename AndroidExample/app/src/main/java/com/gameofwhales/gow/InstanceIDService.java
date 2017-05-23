package com.gameofwhales.gow;

import android.util.Log;

import com.gameofwhales.sdk.GameOfWhales;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "GOW.InstanceIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        GameOfWhales.UpdateToken(refreshedToken);
    }
}