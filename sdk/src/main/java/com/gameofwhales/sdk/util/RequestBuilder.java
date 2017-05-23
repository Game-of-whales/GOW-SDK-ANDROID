package com.gameofwhales.sdk.util;

import com.gameofwhales.sdk.L;
import com.gameofwhales.sdk.protocol.commands.Command;
import com.gameofwhales.sdk.util.net.HTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestBuilder {
    private static final String     TAG = "RequestBuilder";
    private static final String     SERVER_URL = "https://api.gameofwhales.com:8443";


    public static void create(HTTP http, DataStorage data, Command command, InternalResponseListener listener) throws JSONException
    {
        JSONObject args = command.getArgs();
        if (args == null) {
            L.e(TAG, "Args is null for " + command.getID());
            return;
        }

        args.put("game", data.getGameID());
        args.put("user", data.getAdvID());

        final String url = SERVER_URL + "/" + command.getID();

        L.i(TAG, "Sending: " + url);
        L.i(TAG, "Args " + args.toString());

        command.setState(Command.State.WaitingResponse);
        http.send(url, args.toString(), listener);
    }
}
