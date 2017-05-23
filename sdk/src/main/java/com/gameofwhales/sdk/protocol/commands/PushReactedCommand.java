package com.gameofwhales.sdk.protocol.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PushReactedCommand extends Command {
    public static String ID = "sdk.push";

    private String pushID;

    public PushReactedCommand(String pushID)
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

            args.put("reacted", array);
            return args;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
