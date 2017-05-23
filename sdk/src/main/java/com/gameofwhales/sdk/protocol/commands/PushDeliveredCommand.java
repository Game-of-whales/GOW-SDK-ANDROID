package com.gameofwhales.sdk.protocol.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PushDeliveredCommand extends Command {

    public static String ID = "sdk.push";

    private String pushID;

    public PushDeliveredCommand(String pushID)
    {
        this.pushID = pushID;
        this.id = ID;
    }

    @Override
    public JSONObject getArgs()
    {
        try {
            JSONObject args = new JSONObject();
            JSONArray array = new JSONArray();
            array.put(pushID);

            args.put("delivered", array);
            return args;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
