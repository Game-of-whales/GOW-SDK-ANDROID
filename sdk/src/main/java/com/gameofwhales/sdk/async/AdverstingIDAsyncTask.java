package com.gameofwhales.sdk.async;


import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;


public class AdverstingIDAsyncTask extends AsyncTask<Void, Void, String>
{
    private static String TAG = "GOW.AdverstingIDAsyncTask";
    private AdverstingIDAsyncTaskListener listener;
    private Context context;

    public AdverstingIDAsyncTask(Context context, AdverstingIDAsyncTaskListener listener)
    {
        this.listener = listener;
        this.context = context;
    }


    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            AdvertisingIdClient.Info idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());

            if (idInfo != null)
            {
                return idInfo.getId();
            }
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(final String id)
    {
        if (listener != null)
        {
            if (id == null || id.isEmpty())
            {
                listener.onFailure();
            }
            else
            {
                listener.onSuccess(id);
            }
        }
    }
}
