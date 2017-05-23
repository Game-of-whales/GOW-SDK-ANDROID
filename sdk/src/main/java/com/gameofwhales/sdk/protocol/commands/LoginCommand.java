package com.gameofwhales.sdk.protocol.commands;

import com.gameofwhales.sdk.BuildConfig;

import org.json.JSONObject;

/**
 * Created by denis on 28.04.17.
 */

import java.util.Calendar;

public class LoginCommand extends Command {

    public static String ID = "sdk.login";
    String timezone;
    String platform = "android";
    String version;

    public LoginCommand()
    {
        super.id = ID;
        timezone = Calendar.getInstance().getTimeZone().getID();
        version = BuildConfig.VERSION_NAME;
    }

    @Override
    public JSONObject getArgs()
    {
        try
        {
            JSONObject args = new JSONObject();
            args.put("timezone", timezone);
            args.put("platform", platform);
            args.put("version", version);
            return args;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
