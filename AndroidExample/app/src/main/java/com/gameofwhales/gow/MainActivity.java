package com.gameofwhales.gow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gameofwhales.sdk.GameOfWhales;
import com.gameofwhales.sdk.GameOfWhalesListener;
import com.gameofwhales.sdk.Replacement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AppBillingListener, Button.OnClickListener{
    private static int buttonsCount = 5;
    private static final String TAG = "GOW.Test";
    public static final int BILLING_REQUEST_CODE = 1001;

    private ArrayList<String> products = new ArrayList<String>() {{
        add("product_10");
        add("product_20");
    }};


    class ButtonData
    {
        public Button button;
        public String sku;
        public String offer;
    }

    private List<ButtonData> buttons;

    private AppBilling appBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

        initButtons();
        appBilling = new AppBilling(this, this);
        GameOfWhales.Init(this, gowListener);
    }


    private GameOfWhalesListener gowListener = new GameOfWhalesListener() {

        @Override
        public void onSpecialOfferAppeared(Replacement replacement) {
            Log.i(TAG, "onSpecialOfferAppeared: " + replacement.toString());

            ButtonData data = getButtonData(replacement.originalSku);
            if (data == null)
            {
                Log.e(TAG, "Offer for unknown button! SKU: " + replacement.originalSku);
                return;
            }

            data.offer = replacement.offerProduct.getSku();
            data.button.setText(replacement.offerProduct.getTitle());
        }

        @Override
        public void onSpecialOfferDisappeared(Replacement replacement) {
            Log.i(TAG, "onSpecialOfferDisappeared: " + replacement.toString());

            ButtonData data = getButtonData(replacement.originalSku);
            if (data == null)
            {
                Log.e(TAG, "Offer for unknown button! SKU: " + replacement.originalSku);
                return;
            }

            data.offer = "";

            AppBilling.ItemData itemData = appBilling.getDetail(data.sku);
            data.button.setText(itemData.title);
        }

        @Override
        public void onSpecialOfferPurchased(Replacement replacement) {
            Log.i(TAG, "onSpecialOfferPurchased: " + replacement.toString());
        }

        @Override
        public void onNeedRequestDetails(ArrayList<String> skus) {
            appBilling.requestDetails(skus);
        }

    };

    @Override
    public void onStart()
    {
        super.onStart();
        appBilling.startService();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        appBilling.stopService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BILLING_REQUEST_CODE) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Log.d(TAG, "You have bought the " + sku + ". Excellent choice, adventurer!");
                    GameOfWhales.InAppPurchased(data);

                    String purchaseToken = jo.getString("purchaseToken");
                    appBilling.consume(purchaseToken);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.e(TAG, "Purchasing error: " + data.getExtras().toString());
            }
        }
    }


    private void initButtons()
    {
        buttons = new ArrayList<>();

        for(int i=1; i<=buttonsCount; i++)
        {
            String buttonID = "button" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button button = ((Button) findViewById(resID));
            if (i <= products.size())
            {
                ButtonData data = new ButtonData();
                data.offer = "";
                data.sku = products.get(i - 1);
                data.button = button;
                button.setText(data.sku);
                button.setOnClickListener(this);
                buttons.add(data);
                button.setVisibility(View.VISIBLE);
            }
            else
            {
                button.setVisibility(View.GONE);
            }
        }
    }

    private ButtonData getButtonData(String sku)
    {
        for(ButtonData data : buttons)
        {
            if (data.sku.equals(sku))
            {
                return data;
            }
        }

        return null;
    }

    @Override
    public void onClick(View v) {

        //testNotification();

        ButtonData data = findDataByButton(v);
        if (data != null)
        {
            String sku = data.offer.isEmpty() ? data.sku : data.offer;
            Log.i(TAG, "Purchasing " + sku);
            appBilling.purchase(sku);
        }
    }

    @Override
    public void onServiceConnected() {

        appBilling.requestDetails(products);
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onDetails(ArrayList<AppBilling.ItemData> newDetails) {

        for(AppBilling.ItemData data : newDetails)
        {
            Button button = findButtonBySKU(data.id);
            if (button != null)
            {
                button.setText(data.title);
            }
        }
    }

    private ButtonData findDataByButton(View view)
    {
        for(ButtonData data: buttons)
        {
            if (data.button == view)
            {
                return data;
            }
        }

        Log.e(TAG, "Data not found for view: " + view.toString());
        return null;
    }

    private Button findButtonBySKU(String sku)
    {
        for(ButtonData data : buttons)
        {
            if (data.sku.equals(sku))
                return data.button;

            if (data.offer.equals(sku))
                return data.button;
        }

        return null;
    }

}
