package com.gameofwhales.sdk.protocol.commands;

import org.json.JSONObject;

public class SpecialOffersCommand extends Command{
    public static String ID = "sdk.sos";


    public SpecialOffersCommand()
    {
        this.id = ID;
    }

    @Override
    public JSONObject getArgs()
    {
        try
        {
            JSONObject args = new JSONObject();
            return args;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
