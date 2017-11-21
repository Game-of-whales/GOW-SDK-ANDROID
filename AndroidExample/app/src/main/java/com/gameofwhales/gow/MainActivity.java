package com.gameofwhales.gow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.Toast;

import com.gameofwhales.gow.base.AppBilling;
import com.gameofwhales.gow.base.PlayerInfo;
import com.gameofwhales.gow.views.BankFragment;
import com.gameofwhales.gow.views.PlayerFragment;
import com.gameofwhales.sdk.GameOfWhales;
import com.gameofwhales.sdk.SpecialOffer;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gameofwhales.gow.views.BankFragment.BILLING_REQUEST_CODE;

public class MainActivity extends FragmentActivity{
    private static final String TAG = "GOW.Test";
    static final int PAGE_COUNT = 2;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

        final String store = GameOfWhales.STORE_GOOGLEPLAY;
        //final String store = GameOfWhales.STORE_SAMSUNG;
        GameOfWhales.Init(this, store, null);

        PlayerInfo.Init(this);

        //Request GCM token
        //GameOfWhales.SetAndroidProjectID("YOUR_ANDROID_PROJECT_ID____OR____FIREBASE_SENDER_ID");


        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (BankFragment.instance != null)
                    BankFragment.instance.OnUpdate();

                if (PlayerFragment.instance != null)
                    PlayerFragment.instance.OnUpdate();

            }
        });
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Bank";

            if (position == 1)
                return "Player Info";

            return "Unknown";
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new BankFragment();

            if (position == 1)
                return new PlayerFragment();

            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BILLING_REQUEST_CODE) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject inappData = new JSONObject(purchaseData);
                    String transactionID = inappData.getString("purchaseToken");
                    String sku = inappData.getString("productId");
                    Log.d(TAG, "You have bought the " + sku + ". Excellent choice, adventurer!");
                    GameOfWhales.InAppPurchased(data);

                    int addMoney = PlayerInfo.getProductMoney(sku);

                    SpecialOffer rep = GameOfWhales.GetSpecialOffer(sku);
                    if (rep != null)
                        addMoney = (int)(addMoney * rep.countFactor);

                    PlayerInfo.instance.addMoney(addMoney);
                    PlayerInfo.instance.addPurchase(transactionID, sku, addMoney);

                    GameOfWhales.Acquire("money", addMoney, sku, 1, "shop");

                    AppBilling.instance.consume(transactionID);

                    showToast("You have bought the " + sku);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.e(TAG, "Purchasing error: " + data.getExtras().toString());
                showToast("Purchasing error");
            }
        }
    }


    private void showToast(String message)
    {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
