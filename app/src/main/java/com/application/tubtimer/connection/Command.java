package com.application.tubtimer.connection;

import com.application.tubtimer.database.Timer;
import com.google.gson.Gson;

public class Command {
    static Gson gson = new Gson();

    byte[] getBytes(){
        return ("&"+gson.toJson(this)).getBytes();
    }

    public Command(int action, Timer timer) {
        this.action = action;
        this.timer = timer;
    }

    public static Command parseCommand(String command){
        String data = command.substring(1);
        return  gson.fromJson(data,Command.class);
    }

    public int action;
//    static int version;
    public Timer timer;
}
