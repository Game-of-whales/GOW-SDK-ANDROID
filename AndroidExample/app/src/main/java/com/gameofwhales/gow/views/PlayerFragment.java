package com.gameofwhales.gow.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gameofwhales.gow.R;
import com.gameofwhales.gow.base.PlayerInfo;
import com.gameofwhales.sdk.GameOfWhales;
import com.gameofwhales.sdk.SpecialOffer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PlayerFragment extends Fragment implements Button.OnClickListener{

    public static PlayerFragment instance;
    private Handler updateHandler = new Handler();


    public void OnUpdate()
    {
        updateParametersView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.player_fragment, null);

        registerButton(R.id.buy_item_1, view);
        registerButton(R.id.buy_item_2, view);
        registerButton(R.id.nextlocation_button, view);
        registerButton(R.id.levelup_button, view);
        return view;
    }

    private void registerButton(@IdRes int id, View view)
    {
        Button btn = (Button)view.findViewById(id);
        btn.setOnClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        runUpdate();
        updateParametersView();
        reportChanges();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.buy_item_1 || v.getId() == R.id.buy_item_2)
        {
            String itemID = "item1";
            if (v.getId() == R.id.buy_item_2)
            {
                itemID = "item2";
            }

            int cost = PlayerInfo.getItemCost(itemID);

            SpecialOffer rep = GameOfWhales.GetSpecialOffer(itemID);
            if (rep != null)
            {
                cost *= rep.priceFactor;
            }

            if (PlayerInfo.instance.canBuy(cost))
            {
                PlayerInfo.instance.decMoney(cost);
                updateParametersView();
                showToast("OK!");
                GameOfWhales.Consume("money", cost, itemID, 1, "shop");
                reportChanges();
            }
            else
            {
                showToast("Not enough money!");
            }
        }

        if (v.getId() == R.id.nextlocation_button)
        {
            PlayerInfo.instance.nextLocation();
            updateParametersView();
            reportChanges();
        }

        if (v.getId() == R.id.levelup_button)
        {
            PlayerInfo.instance.nextLevel();
            updateParametersView();
            reportChanges();
        }


    }

    public void reportChanges()
    {
        HashMap<String, Object> changes = new HashMap<>();
        changes.put("class", PlayerInfo.instance.getUserClass());
        changes.put("gender", Boolean.valueOf(PlayerInfo.instance.getGender()));
        changes.put("location", PlayerInfo.instance.getLocation());
        changes.put("level", Integer.valueOf(PlayerInfo.instance.getLevel()));
        GameOfWhales.Profile(changes);
    }

    public void updateParametersView()
    {
        View v = getView();

        if (v == null)
            return;


        TextView money = (TextView) v.findViewById(R.id.moneyView);
        money.setText("Money: " + String.valueOf(PlayerInfo.instance.getMoney()));

        Button level = (Button) v.findViewById(R.id.levelup_button);
        level.setText("LEVEL UP ( " + String.valueOf(PlayerInfo.instance.getLevel() + " )"));

        TextView gender = (TextView) v.findViewById(R.id.sexView);
        gender.setText("Gender: " + String.valueOf(PlayerInfo.instance.getGender() ? "Man" : "Woman"));

        TextView classView = (TextView) v.findViewById(R.id.classView);
        classView.setText("Class: " + String.valueOf(PlayerInfo.instance.getUserClass()));

        Button location = (Button) v.findViewById(R.id.nextlocation_button);
        location.setText("NEXT LOCATION ( " + String.valueOf(PlayerInfo.instance.getLocation()) + " )");

        Button button_item1 = (Button) v.findViewById(R.id.buy_item_1);
        updateButton(button_item1, "item1");

        Button button_item2 = (Button) v.findViewById(R.id.buy_item_2);
        updateButton(button_item2, "item2");
    }

    private void updateButton(Button button, final String itemID)
    {
        String text = itemID + "\n";

        int cost = PlayerInfo.getItemCost(itemID);

        SpecialOffer rep = GameOfWhales.GetSpecialOffer(itemID);
        boolean useOffer = rep != null && rep.hasPriceFactor();

        if (useOffer)
        {
            cost *= rep.priceFactor;
        }

        text += "Cost: " + String.valueOf(cost);

        if (useOffer)
        {
            text += " ( " + String.valueOf( (int)(100 - rep.priceFactor * 100)) + "% Discount! )";
        }

        if (useOffer)
        {
            long left = rep.getLeftTime();
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(left),
                    TimeUnit.MILLISECONDS.toMinutes(left) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(left)),
                    TimeUnit.MILLISECONDS.toSeconds(left) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(left)));
            text += "\nTime left: " + hms;
        }

        button.setText(text);
    }



    private void showToast(String message)
    {
        Context context = getActivity().getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void runUpdate() {
        updateHandler.postDelayed(updateRunner, 1000);
    }

    private Runnable updateRunner = new Runnable() {
        @Override
        public void run() {
            updateParametersView();
            runUpdate();
        }
    };
}
