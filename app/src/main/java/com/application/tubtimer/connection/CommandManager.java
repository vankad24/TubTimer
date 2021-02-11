package com.application.tubtimer.connection;

import android.content.DialogInterface;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.adroitandroid.near.connect.NearConnect;
import com.adroitandroid.near.discovery.NearDiscovery;
import com.adroitandroid.near.model.Host;
import com.application.tubtimer.activities.SearchActivity;
import com.application.tubtimer.adapters.DeviceAdapter;
import com.application.tubtimer.database.Timer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Set;

public class CommandManager {
    private static final long DISCOVERABLE_TIMEOUT_MILLIS = 20000; //вызывается после makeDiscoverable() (стать видимым)
    private static final long DISCOVERY_TIMEOUT_MILLIS = 10000; // вызывается после startDiscovery()
    private static final long DISCOVERABLE_PING_INTERVAL_MILLIS = 5000;
    public static final String MESSAGE_REQUEST_CONNECT = "connect";
    public static final String MESSAGE_RESPONSE_DECLINE_CONNECT = "decline_request";
    public static final String MESSAGE_RESPONSE_ACCEPT_CONNECT = "accept_request";
    public static final String MESSAGE_REQUEST_TOAST = "toast";

    static Gson gson = new Gson();

    public static final int ACTION_ADD = 0;
    public static final int ACTION_DELETE = 1;
    public static final int ACTION_CHANGE = 2;

    SearchActivity activity;
    public NearDiscovery nearDiscovery;
    private NearConnect nearConnection;

    private boolean mDiscovering;

    ArrayList<Host> connectedHosts;
    ArrayList<Host> foundHosts;
    DeviceAdapter adapter;

    public CommandManager(final SearchActivity activity) {
        connectedHosts = new ArrayList<>();
        foundHosts = new ArrayList<>();
        this.activity = activity;

        adapter = activity.adapter;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nearDiscovery = new NearDiscovery.Builder()
                            .setContext(activity.getApplicationContext())
                            .setDiscoverableTimeoutMillis(DISCOVERABLE_TIMEOUT_MILLIS)
                            .setDiscoveryTimeoutMillis(DISCOVERY_TIMEOUT_MILLIS)
                            .setDiscoverablePingIntervalMillis(DISCOVERABLE_PING_INTERVAL_MILLIS)
                            .setDiscoveryListener(getNearDiscoveryListener(), Looper.getMainLooper())
                            .build();

                    nearConnection = new NearConnect.Builder()
                            .fromDiscovery(nearDiscovery)
                            .setContext(activity.getApplicationContext())
                            .setListener(getNearConnectListener(), Looper.getMainLooper())
                            .build();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void send(int action , Timer... timers){
        for (Host host:connectedHosts) {
            for (Timer t : timers) {
                nearConnection.send(Command.getBytes(action, t.getStringData()),host);
            }
        }
    }


    byte[] getBytesCommand(int action, String timer){
        return ("&"+action+":"+timer).getBytes();
    }

    static Timer parseCommand(String command){
        String[] data = command.substring(1).split(":",1);
        Timer timer = gson.fromJson(data[1],Timer.class);
        int action = Integer.parseInt(data[0]);

        switch (action){
            case ACTION_ADD:
                //todo
        }

        return timer;
    }













    @NonNull
    private NearDiscovery.Listener getNearDiscoveryListener() {
        return new NearDiscovery.Listener() {
            @Override
            public void onPeersUpdate(Set<? extends Host> hosts) {
//                nearConnection.send(MESSAGE_REQUEST_CONNECT.getBytes(),host);
//                foundHosts = new ArrayList<>(hosts);
                Toast.makeText(activity, "Host", Toast.LENGTH_SHORT).show();

                ArrayList<Host> list = new ArrayList<>();
                for (Host host: hosts){
                    if (!connectedHosts.contains(host)) list.add(host);
                }
                adapter.setData(list);
            }


            @Override
            public void onDiscoveryTimeout() {
//                Toast.makeText(activity, "No other participants found", Toast.LENGTH_SHORT).show();

                mDiscovering = false;
            }

            @Override
            public void onDiscoveryFailure(Throwable e) {
                Toast.makeText(activity, "Something went wrong while searching for participants", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDiscoverableTimeout() {
                Toast.makeText(activity, "Устройства не найдены", Toast.LENGTH_LONG).show();
            }
        };
    }


    @NonNull
    private NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(byte[] bytes, final Host sender) {
                if (bytes != null) {
                    String message = new String(bytes);
                    if (message.startsWith("&")){
                        parseCommand(message);
                    }else
                    switch (message){
                        case MESSAGE_RESPONSE_ACCEPT_CONNECT:
                            activity.connectedDevice.setVisibility(View.VISIBLE);
                            activity.connectedDevice.setText("Подключено к "+sender.getName());
                            activity.recycler.setVisibility(View.GONE);
                            break;
                        case MESSAGE_REQUEST_CONNECT:
                            new AlertDialog.Builder(activity)
                                    .setMessage(sender.getName() + " хочет подключиться к вам")
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            nearConnection.send(MESSAGE_RESPONSE_ACCEPT_CONNECT.getBytes(), sender);
                                            connectedHosts.add(sender);
                                            //todo подключённые устройства
                                        }
                                    })
                                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).create().show();
                            break;
                    }
                }
            }

            @Override
            public void onStartListenFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSendFailure(Throwable throwable, long l) {
                throwable.printStackTrace();
            }

            @Override
            public void onSendComplete(long l) {

            }

        };
    }

    public void sendConnectRequest(Host host) {
        if (!nearConnection.isReceiving()) {
            nearConnection.startReceiving();
        }
        nearConnection.send(MESSAGE_REQUEST_CONNECT.getBytes(), host);
    }
}
