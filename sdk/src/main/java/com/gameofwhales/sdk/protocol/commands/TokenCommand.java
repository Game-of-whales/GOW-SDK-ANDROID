package com.gameofwhales.sdk.protocol.commands;

import org.json.JSONObject;

public class TokenCommand extends Command{

    public static String ID = "sdk.token";
    String token;

    public TokenCommand(String token)
    {
        this.id = ID;
        this.token = token;
    }


    @Override
    public JSONObject getArgs() {

        try
        {
            JSONObject args = new JSONObject();
            args.put("token", token);
            return args;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


}
