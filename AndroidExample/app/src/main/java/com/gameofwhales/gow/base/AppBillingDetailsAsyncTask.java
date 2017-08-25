package com.gameofwhales.gow.base;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;

import java.util.ArrayList;


public class AppBillingDetailsAsyncTask extends AsyncTask<Void, Void, Bundle> {

    private static String TAG = "GOW.BillingTask";
    AppBilling billing;
    IInAppBillingService service;
    Activity activity;
    ArrayList<String> skuIDs;

    AppBillingDetailsAsyncTask(Activity activity, AppBilling billing, ArrayList<String> skuIDs)
    {
        this.billing = billing;
        this.activity = activity;
        this.service = billing.getService();
        this.skuIDs = skuIDs;
    }


    @Override
    protected Bundle doInBackground(Void... params) {

        final String pck = activity.getPackageName();

        if (service == null)
        {
            Log.e(TAG, "Service is null");
            return null;
        }

        Bundle query = new Bundle();
        query.putStringArrayList("ITEM_ID_LIST", skuIDs);

        Bundle details = null;
        try {
            details = service.getSkuDetails(3, pck, "inapp", query);
        } catch (RemoteException e)
        {
            Log.e(TAG, "RemoteException while getting details");
        }

        int response = details.getInt("RESPONSE_CODE");
        if (response == 0)
        {
            return details;
        }
        else
        {
            Log.e(TAG, "Request details response code: " + response);
        }

        return null;
    }

    @Override
    protected void onPostExecute(final Bundle bundle)
    {
        if (billing != null && bundle != null)
        {
            billing.setDetails(bundle);
        }
    }
}
