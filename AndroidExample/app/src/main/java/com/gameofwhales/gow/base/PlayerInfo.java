package com.gameofwhales.gow.base;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerInfo {

    final static String FILE_NAME = "ParamsAdapterPrefs";

    public static PlayerInfo instance;

    Activity activity;
    int m_Level = 1;
    String m_Class = "";
    boolean m_Gender;
    String m_Location = "A";
    int m_LocationCode = 0;

    int m_Money = 1000;

    SharedPreferences sharedPref;

    public int getMoney()
    {
        return m_Money;
    }

    public boolean canBuy(int cost)
    {
        return m_Money >= cost;
    }

    public void decMoney(int cost)
    {
        m_Money -= cost;
        save();
    }

    public void addMoney(int value)
    {
        m_Money += value;
        save();
    }

    public static int getItemCost(final String item)
    {
        if (item.equals("item1"))
        {
            return 1000;
        }
        else
        if (item.equals("item2"))
        {
            return 2000;
        }

        return 1;
    }

    public static int getProductMoney(final String product)
    {
        if (product.startsWith("product_5"))
        {
            return 250;
        }
        else if (product.startsWith("product_10"))
        {
            return 750;
        }
        else if (product.startsWith("product_20"))
        {
            return 2000;
        }
        return 1;
    }

    public String getLocation()
    {
        return m_Location;
    }

    public int getLevel()
    {
        return m_Level;
    }

    public boolean getGender()
    {
        return m_Gender;
    }

    public String getUserClass()
    {
        return m_Class;
    }

    public static void Init(Activity activity) {
        instance = new PlayerInfo(activity);
    }

    private PlayerInfo(Activity activity)
    {
        this.activity = activity;
        sharedPref = activity.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        m_Gender = Math.random() % 2 == 0 ? true : false;
        String[] classes = {"Warrior", "Wizard", "Rogue"};
        m_Class = classes[(int)Math.random() % 3];

        load();
        save();
    }


    public void nextLevel()
    {
        m_Level ++;
        save();
    }

    void updateLocation()
    {
        char A = 'A';
        m_Location = String.valueOf((char)((int)(A) + m_LocationCode));
    }

    public void nextLocation()
    {
        m_LocationCode++;
        if (m_LocationCode >= 32)
            m_LocationCode = 0;

        updateLocation();
        save();
    }

    void load()
    {
        m_Level = sharedPref.getInt("level", m_Level);
        m_LocationCode = sharedPref.getInt("location", m_LocationCode);
        m_Class = sharedPref.getString("class", m_Class);
        m_Gender = sharedPref.getBoolean("gender", m_Gender);
        m_Money = sharedPref.getInt("money", m_Money);
        updateLocation();
    }

    void save()
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("class", m_Class);
        editor.putInt("location", m_LocationCode);
        editor.putInt("level", m_Level);
        editor.putBoolean("gender", m_Gender);
        editor.putInt("money", m_Money);
        editor.commit();
    }

    public void addPurchase(final String transactionID, final String product, int money)
    {
        JSONObject object = new JSONObject();
        try {
            object.put("product", product);
            object.put("money", money);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(transactionID, object.toString());
        editor.commit();
    }

    public void refundPurchase(final String transactionID)
    {
        try {
            JSONObject object = new JSONObject(sharedPref.getString(transactionID, "{}"));
            if (object.has("money"))
            {
                int money = object.getInt("money");
                decMoney(money);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(transactionID);
                editor.commit();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
