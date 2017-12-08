package com.gameofwhales.gow.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.gameofwhales.gow.R;
import com.gameofwhales.gow.base.AppBilling;
import com.gameofwhales.gow.base.AppBillingListener;
import com.gameofwhales.gow.base.PlayerInfo;
import com.gameofwhales.sdk.GameOfWhales;
import com.gameofwhales.sdk.GameOfWhalesListener;
import com.gameofwhales.sdk.SpecialOffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class BankFragment extends Fragment implements AppBillingListener, Button.OnClickListener {
    private static int buttonsCount = 4;

    private static final String TAG = "BankFragment";
    public static final int BILLING_REQUEST_CODE = 1001;

    public static BankFragment instance;

    private Handler timer = new Handler();


    class ButtonData
    {
        public Button button;
        public String sku;
        public float priceFactor;
        public float countFactor;
        public int coins;
        public Date finishedAt;
    }

    private List<ButtonData> buttons;

    private ArrayList<String> products = new ArrayList<String>() {{
        add("product_10");
        add("product_20");
    }};

    private AppBilling appBilling;

    public void OnUpdate()
    {
        for(ButtonData btn : buttons)
        {
            UpdateButtonText(btn);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        AppBilling.Init(getActivity(), this);
        appBilling = AppBilling.instance;

        runTimer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bank_fragment, null);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        initButtons();
        GameOfWhales.getInstance().addListener(gowListener);
    }




    private GameOfWhalesListener gowListener = new GameOfWhalesListener() {

        @Override
        public void onSpecialOfferAppeared(SpecialOffer specialOffer) {
            Log.i(TAG, "onSpecialOfferAppeared: " + specialOffer.toString());

            ButtonData data = getButtonData(specialOffer.product);
            if (data == null)
            {
                Log.e(TAG, "Offer for unknown button! SKU: " + specialOffer.product);
                return;
            }

            data.countFactor = specialOffer.countFactor;
            data.priceFactor = specialOffer.priceFactor;
            data.finishedAt = specialOffer.finishedAt;
            UpdateButtonText(data);
        }

        @Override
        public void onSpecialOfferDisappeared(SpecialOffer specialOffer) {
            Log.i(TAG, "onSpecialOfferDisappeared: " + specialOffer.toString());

            ButtonData data = getButtonData(specialOffer.product);
            if (data == null)
            {
                Log.e(TAG, "Offer for unknown button! SKU: " + specialOffer.product);
                return;
            }

            data.priceFactor = 1f;
            data.countFactor = 1f;
            UpdateButtonText(data);

        }


        @Override
        public void onPushDelivered(SpecialOffer specialOffer, String campID, String title, String message) {
            //show message and send reacted
            showDialog(getActivity().getApplicationContext(), campID, title, message);
        }

        @Override
        public void onPurchaseVerified(final String transactionID, final String state) {

            showToast("Purchase verify state: " + state);
            if (state.equals(GameOfWhales.VERIFY_STATE_ILLEGAL))
            {
                PlayerInfo.instance.refundPurchase(transactionID);
            }
        }
    };

    private void showDialog(final Context context, final String campID, final String title, final String message)
    {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setNegativeButton("Ok!",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        GameOfWhales.PushReacted(campID);
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//Using for example purposes
                alert.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
    public void onClick(View v) {
        ButtonData data = findDataByButton(v);
        if (data != null)
        {
            Log.i(TAG, "Purchasing " + data.sku);
            appBilling.purchase(data.sku);
        }

    }

    private void initButtons()
    {
        buttons = new ArrayList<>();

        for(int i=1; i<=buttonsCount; i++)
        {
            String buttonID = "button" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
            Button button = ((Button) getView().findViewById(resID));
            if (i <= products.size())
            {
                ButtonData data = new ButtonData();
                data.countFactor = 1f;
                data.priceFactor = 1f;
                data.sku = products.get(i - 1);
                data.coins = PlayerInfo.getProductMoney(data.sku);
                data.button = button;
                button.setOnClickListener(this);
                buttons.add(data);
                button.setVisibility(View.VISIBLE);
                UpdateButtonText(data);

            }
            else
            {
                button.setVisibility(GONE);
            }
        }
    }

    private void UpdateButtonText(ButtonData data)
    {
        if (data.button == null)
            return;

        String sku = data.sku;

        AppBilling.ItemData itemData = appBilling.getDetail(sku);
        String text = "";
        if (itemData == null)
            text = sku;
        else
            text = itemData.title;

        if (data.countFactor > 1.0f)
        {
            text += "\n\nSpecial Offer!\n";
        }

        text += "\n Coins: " + String.valueOf(data.coins);
        if (data.countFactor > 1.0f)
        {
            text += " + " + String.valueOf((int)(data.coins * data.countFactor - data.coins)) + "(" + String.valueOf((int)((data.countFactor - 1) * 100)) + "% Bonus!)";
        }

        if (data.finishedAt != null)
        {
            Date current = new Date(System.currentTimeMillis());//Calendar.getInstance().getTime();
            long diff = data.finishedAt.getTime() - current.getTime();
            if (diff > 0)
            {
               String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(diff),
                                TimeUnit.MILLISECONDS.toMinutes(diff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
                                TimeUnit.MILLISECONDS.toSeconds(diff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
                text += "\n\nTime left: " + hms;
            }
        }

        data.button.setText(text);
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
    public void onServiceConnected() {
        appBilling.requestDetails(products);
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onDetails(ArrayList<AppBilling.ItemData> newDetails) {
        for(ButtonData data: buttons)
        {
            UpdateButtonText(data);
        }
    }


    private void showToast(String message)
    {
        Context context = getActivity().getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void runTimer()
    {
        timer.postDelayed(timerRunner, 1000);
    }

    private Runnable timerRunner = new Runnable() {
        @Override
        public void run() {
            BankFragment.this.OnUpdate();
            runTimer();
        }
    };
}
