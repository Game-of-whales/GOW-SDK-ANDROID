package com.gameofwhales.gow.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.gameofwhales.sdk.GameOfWhales;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.gameofwhales.gow.views.BankFragment.BILLING_REQUEST_CODE;

public class AppBilling {

    private static String TAG = "Test.GOW.AppBilling";
    private static String SERVICE_BIND = "com.android.vending.billing.InAppBillingService.BIND";
    private static String SERVICE_PACKAGE = "com.android.vending";
    private static String BUY_INTENT = "BUY_INTENT";

    public static AppBilling instance;

    private String payload = "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ";
    private AppBillingListener listener;
    private Activity activity;

    IInAppBillingService service;
    ServiceConnection serviceConn;

    public static void Init(Activity activity, final AppBillingListener listener)
    {
        instance = new AppBilling(activity, listener);
    }

    public class ItemData
    {
        public String id;
        public String price;
        public float micros;
        public String curCode;
        public String title;
    }

    Map<String, ItemData> prices = new HashMap<String, ItemData>();


    public IInAppBillingService getService()
    {
        return  service;
    }


    public ItemData getDetail(final String sku)
    {
        /*if (!prices.containsValue(sku))
        {
            return null;
        }*/

        return prices.get(sku);
    }

    public void setDetails(Bundle data)
    {

        GameOfWhales.DetailsReceived(data);

        ArrayList<String> detailsList = data.getStringArrayList("DETAILS_LIST");
        try
        {
            ArrayList<ItemData> newDetails = new ArrayList<>();
            for(final String item : detailsList)
            {
                //Example:
                // {"productId":"product_10",
                // "type":"inapp",
                // "price":"599,00 ₽",
                // "price_amount_micros":599000000,
                // "price_currency_code":"RUB",
                // "title":"product 10 (Game of whales)",
                // "description":"product for 10USD"}

                //Storing data
                JSONObject json = new JSONObject(item);
                Log.i(TAG, "OnNewDetails: " + json.toString());

                final String sku = json.getString("productId");

                ItemData itemData = new ItemData();
                itemData.id = sku;
                itemData.price = json.getString("price");
                itemData.curCode = json.getString("price_currency_code");
                itemData.micros = (float)( Double.valueOf(json.getString("price_amount_micros")) / 1000000 );
                itemData.title = json.getString("title");
                prices.put(sku, itemData);

                newDetails.add(itemData);
            }

            if (listener != null)
                listener.onDetails(newDetails);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private AppBilling(Activity activity, final AppBillingListener listener)
    {
        this.activity = activity;
        this.listener = listener;

        serviceConn = new ServiceConnection()
        {
            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                service = null;
                Log.d(TAG, "onServiceDisconnected");

                if (listener != null)
                    listener.onServiceDisconnected();
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                AppBilling.this.service = IInAppBillingService.Stub.asInterface(service);
                Log.d(TAG, "onServiceConnected");

                if (listener != null)
                    listener.onServiceConnected();

                consumeAllPurchases();

                //TODO: requestDetails();

            }
        };



    }

    public void requestDetails(ArrayList<String> skus)
    {
        if (service == null)
        {
            return;
        }

        Log.i(TAG, "requesting details: " + skus.toString());

        AppBillingDetailsAsyncTask task = new AppBillingDetailsAsyncTask(activity, this, skus);
        task.execute();
    }

    public boolean verify(String data, String signature)
    {
        /* Payment verification code */

        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == BILLING_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK)
                return;

            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            if (responseCode != 0)
                return;

            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            if (verify(purchaseData, dataSignature)) {
                {
                    //processPaymentData(purchaseData, dataSignature);
                }
            }
        }
    }

    public void consume(final String purchaseToken)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    int response = service.consumePurchase(3, activity.getPackageName(), purchaseToken);
                    if (response == 0)
                    {
                        Log.i(TAG, "Item consumed");
                    }

                } catch (RemoteException e)
                {

                }
            }
        });
    }

    public void purchase(final String sku)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (service == null)
                {
                    Log.e(TAG, "Service is null");
                    return;
                }

                try
                {
                    Log.i(TAG, "Purchase " + sku);
                    final String pck = activity.getPackageName();
                    Bundle buyIntentBundle = service.getBuyIntent(3, pck, sku, "inapp", payload);
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable(BUY_INTENT);

                    if (buyIntentBundle == null)
                    {
                        return;
                    }

                    activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                            BILLING_REQUEST_CODE,
                            new Intent(),
                            Integer.valueOf(0),
                            Integer.valueOf(0),
                            Integer.valueOf(0));

                } catch (RemoteException e)
                {
                    Log.e(TAG, "Remote exception");
                    e.printStackTrace();
                } catch (IntentSender.SendIntentException e)
                {
                    e.printStackTrace();
                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startService()
    {
        Intent serviceIntent = new Intent(SERVICE_BIND);
        serviceIntent.setPackage(SERVICE_PACKAGE);
        activity.bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    public void stopService()
    {
        if (service != null)
        {
            activity.stopService(new Intent(activity, ServiceConnection.class));
            activity.unbindService(serviceConn);
        }
    }




    private void consumeAllPurchases()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (service == null)
                {
                    Log.e(TAG, "consumeAllPurchases: Service is null");
                    return;
                }

                Bundle ownedItems = null;
                try
                {
                    ownedItems = service.getPurchases(3, activity.getPackageName(), "inapp", null);
                }
                 catch (RemoteException e)
                {
                    e.printStackTrace();
                }

                int response = ownedItems.getInt("RESPONSE_CODE");
                if (response == 0)
                {
                    ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    ArrayList<String>  purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                    ArrayList<String>  signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                    String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                    for (int i = 0; i < purchaseDataList.size(); ++i) {
                        String purchaseData = purchaseDataList.get(i);
                        String signature = signatureList.get(i);
                        String sku = ownedSkus.get(i);

                        try {
                            JSONObject jo = new JSONObject(purchaseData);
                            String purchaseToken = jo.getString("purchaseToken");
                            consume(purchaseToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
    }
}
