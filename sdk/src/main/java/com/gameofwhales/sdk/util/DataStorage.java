package com.gameofwhales.sdk.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.gameofwhales.sdk.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataStorage {

    private static String               TAG = "GOW.DataStorage";
    private static final String         DATA_FILE = "gow.data";
    private static final String         GAME_KEY = "gameOfWhales.gameId";

    private String                      gameID = "";
    private String                      advID = "";
    private String                      token = "";
    private Context context;

    public DataStorage(Context context, String gameID)
    {
        this.gameID = gameID;
        this.context = context;

        loadData();
        readGameIDFromManifest();
    }

    public String getGameID()
    {
        return gameID;
    }

    public String getAdvID()
    {
        return advID;
    }

    public String getToken()
    {
        return token;
    }

    public void setGameID(final String gameID)
    {
        this.gameID = gameID;
        saveData();
    }

    public void setAdvID(final String advID)
    {
        this.advID = advID;
        saveData();
    }

    public void setToken(final String token)
    {
        this.token = token;
        saveData();
    }

    public boolean isReady()
    {
        boolean isReady = !advID.isEmpty() && !gameID.isEmpty();

        if (L.isDebug)
        {
            L.d(TAG, "isSDKReady called");
            L.d(TAG, "    AdverstingID: " + advID);
            L.d(TAG, "    gameID:       " + gameID);
            L.d(TAG, "    State: " + (isReady ? "Ready" : "Not ready"));
        }

        return isReady;
    }


    private void saveData()
    {
        L.i(TAG, "Saving data...");
        try {
            JSONObject json = new JSONObject();
            json.put("gameID", gameID);
            json.put("userID", advID);
            json.put("token", token);

            FileOutputStream fos = context.openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
            fos.write(json.toString().getBytes());
            fos.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadData()
    {
        L.i(TAG, "Reading data...");
        try {
            StringBuffer datax = new StringBuffer("");
            FileInputStream fIn = context.openFileInput (DATA_FILE);
            InputStreamReader isr = new InputStreamReader ( fIn );
            BufferedReader buffreader = new BufferedReader ( isr );

            String readString = buffreader.readLine ( );
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( );
            }
            isr.close ( );

            JSONObject json = new JSONObject(datax.toString());
            if (gameID == null || gameID.isEmpty())
                gameID = json.getString("gameID");

            advID = json.getString("userID");
            token = json.getString("token");
        } catch ( IOException | JSONException e ) {
        }
    }

    private void readGameIDFromManifest()
    {
        if (gameID == null || gameID.isEmpty())
        {
            try
            {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                gameID = bundle.getString(GAME_KEY);
                saveData();
            } catch (PackageManager.NameNotFoundException e)
            {

            }

            if (gameID == null || gameID.isEmpty())
            {
                L.e(TAG, String.format("GameID is not set! Add <meta-data android:name=\"%s\" android:value=\"YOUR_GAME_ID\" /> to AndroidManifest.xml", GAME_KEY));
            }

        }
    }
}
