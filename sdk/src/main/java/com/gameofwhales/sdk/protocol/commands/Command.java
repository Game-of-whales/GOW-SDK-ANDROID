package com.gameofwhales.sdk.protocol.commands;

import org.json.JSONObject;

import static com.gameofwhales.sdk.protocol.commands.Command.State.Idle;


public abstract class Command {

    public enum State
    {
        Idle,
        WaitingResponse,
        Completed,
    }

    State state = Idle;
    String id;
    JSONObject args;
    long time = 0;
    Command listener;

    public String getID()
    {
        return id;
    }

    public Command()
    {
        time = System.currentTimeMillis();
    }

    public boolean canSend()
    {
        return true;
    }

    public abstract JSONObject getArgs();

    public boolean isIdle()
    {
        return state == Idle;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public State getState()
    {
        return this.state;
    }
}
