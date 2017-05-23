package com.gameofwhales.sdk.util;

import com.gameofwhales.sdk.protocol.commands.Command;

import org.json.JSONObject;

public interface CommandListener {

    void onCommandResponse(Command command, JSONObject response);
}
