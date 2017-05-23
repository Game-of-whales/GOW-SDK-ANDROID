package com.gameofwhales.sdk.util.net;


import android.os.AsyncTask;

import com.gameofwhales.sdk.L;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HTTPAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "HTTPAsyncTask";

    private String url = null;
    private String data = null;
    private int responseCode = -1;
    private String responseData = "";
    private HTTPListener listener;

    public HTTPAsyncTask(final String url, final JSONObject data, HTTPListener listener)
    {
        this(url, data.toString(), listener);
    }

    public HTTPAsyncTask(final String url, final String data, HTTPListener listener)
    {
        this.url = url;
        this.data = data;
        this.listener = listener;
    }


    @Override
    protected Void doInBackground(Void[] params) {

        L.i(TAG, "HTTPAsyncTask sending: " + url + "\n" + data);

        URL Url;
        HttpURLConnection con = null;
        DataOutputStream printout;
        DataInputStream input;


        try {
            Url = new URL(url);
            con = (HttpURLConnection) Url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setRequestProperty("Content-Type", "application/json");
            con.connect();

            printout = new DataOutputStream(con.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(printout, "UTF-8"));
            writer.write(data);
            writer.close();
            printout.close();

            responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = null;

                while((line = br.readLine()) != null)
                {
                    responseData += line;
                }
                L.i(TAG, "response[" + responseCode + "]: " + responseData);
                br.close();
            }
            else
            {
                L.i(TAG, "response error: " + responseCode);
            }
        }
        catch (MalformedURLException e)
        {
            L.e(TAG, "Error while request: " + url);
            e.printStackTrace();
        }
        catch (IOException e) {
            L.e(TAG, "Error while request: " + url);
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void obj)
    {
        if (listener != null)
        {
            listener.OnResponse(this, responseCode != HttpURLConnection.HTTP_OK, responseData);
        }
    }
}
