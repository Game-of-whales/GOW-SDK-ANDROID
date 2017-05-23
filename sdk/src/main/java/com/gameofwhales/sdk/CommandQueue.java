package com.gameofwhales.sdk;

import android.app.Activity;

import com.gameofwhales.sdk.protocol.commands.Command;
import com.gameofwhales.sdk.util.CommandListener;
import com.gameofwhales.sdk.util.DataStorage;
import com.gameofwhales.sdk.util.InternalResponseListener;
import com.gameofwhales.sdk.util.RequestBuilder;
import com.gameofwhales.sdk.util.net.HTTP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CommandQueue {

    private static String       TAG = "CommandQueue";
    private static int          resendDelay = 30000;

    private List<Command> commands = new ArrayList<>();
    CommandListener             listener;
    private final HTTP          http;
    private final Activity      activity;
    private final DataStorage   data;

    CommandQueue(Activity activity, DataStorage data, CommandListener listener)
    {
        this.activity = activity;
        this.listener = listener;
        this.data = data;
        http = new HTTP();
    }

    public void add(Command command)
    {
        send(command);
        /*commands.add(command);
        tryToSendNext();*/
    }

    public void tryToSendNext()
    {
        if (data.isReady() && commands.size() > 0)
        {
            Command command = commands.get(0);
            send(command);
        }
    }

    private void printQueue()
    {
        L.i(TAG, "Command queue");
        for (Command command: commands)
        {
            L.i(TAG, "    " + command.getID());
        }
    }

    private void send(Command command)
    {
        if (command == null)
        {
            L.e(TAG, "Cannot send: Command is null!");
            return;
        }

        if (!data.isReady())
        {
            L.e(TAG, "Send: SDK not ready yet!");
            return;
        }

        if (!command.isIdle())
        {
            L.w(TAG, "Command is not idle " + command.getID());
            return;
        }

        printQueue();

        InternalResponseListener listener = new InternalResponseListener(command) {
            @Override
            public void OnResponse(Command command, boolean error, JSONObject response) {
                L.i(TAG, "OnQueueCommandResponse: " + command.getID() + "       error: " + String.valueOf(error) + " response: " + response);

                if (!error)
                {
                    command.setState(Command.State.Completed);

                    if (CommandQueue.this.listener != null)
                        CommandQueue.this.listener.onCommandResponse(command, response);


                    if (commands.size() > 0)
                        commands.remove(0);

                    tryToSendNext();
                }
                else
                {
                    command.setState(Command.State.Idle);
                    tryToSendAfterDelay();
                }
            }
        };

        try {
            RequestBuilder.create(http, data, command, listener);
        } catch (JSONException e) {
            L.e(TAG, "Request create json error!");
            e.printStackTrace();

        }
    }

    private Runnable delayedTask = new Runnable() {
        @Override
        public void run() {
            tryToSendNext();
        }
    };

    private void tryToSendAfterDelay()
    {
        //new Handler(activity.getMainLooper()).postDelayed(delayedTask, resendDelay);
    }

}
