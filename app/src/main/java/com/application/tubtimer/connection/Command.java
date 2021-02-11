package com.application.tubtimer.connection;

import com.application.tubtimer.database.Timer;
import com.google.gson.Gson;

public class Command {

    static Gson gson = new Gson();

    public static final int ACTION_ADD = 0;
    public static final int ACTION_DELETE = 1;
    public static final int ACTION_CHANGE = 2;

    static byte[] getBytes(int action, String timer){
        return (action+":"+timer).getBytes();
    }

    static Timer parseCommand(byte[] bytes){
        String[] data = new String(bytes).split(":",1);
        Timer timer = gson.fromJson(data[1],Timer.class);
        int action = Integer.parseInt(data[0]);

        return timer;
    }
}
