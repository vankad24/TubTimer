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

public class DiscoveryManager {
    private static final long DISCOVERABLE_TIMEOUT_MILLIS = 60000; //вызывается после makeDiscoverable() (стать видимым)
    private static final long DISCOVERY_TIMEOUT_MILLIS = 10000; // вызывается после startDiscovery()
    private static final long DISCOVERABLE_PING_INTERVAL_MILLIS = 5000;
    public static final String MESSAGE_REQUEST_CONNECT = "connect";
    public static final String MESSAGE_RESPONSE_DECLINE_CONNECT = "decline_request";
    public static final String MESSAGE_RESPONSE_ACCEPT_CONNECT = "accept_request";
    public static final String MESSAGE_REQUEST_PING = "ping";


    SearchActivity activity;
    public NearDiscovery nearDiscovery;
    private NearConnect nearConnection;

    private boolean mDiscovering;

    public ArrayList<Host> connectedHosts = new ArrayList<>();;
    ArrayList<Host> foundHosts = new ArrayList<>();
    DeviceAdapter adapter;

    public void setAdapter(DeviceAdapter adapter) {
        this.adapter = adapter;
    }

    public DiscoveryManager(final SearchActivity activity) {
        this.activity = activity;


        
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









    @NonNull
    private NearDiscovery.Listener getNearDiscoveryListener() {
        return new NearDiscovery.Listener() {
            @Override
            public void onPeersUpdate(Set<? extends Host> hosts) {

                activity.bar.setVisibility(View.GONE);

                foundHosts.clear();
                for (Host host: hosts){
                    nearConnection.send(MESSAGE_REQUEST_PING.getBytes(),host);
                    if (!connectedHosts.contains(host)) foundHosts.add(host);
                }

                adapter.setData(foundHosts);
            }


            @Override
            public void onDiscoveryTimeout() {
//                Toast.makeText(activity, "No other participants found", Toast.LENGTH_SHORT).show();
                if (foundHosts.isEmpty())Toast.makeText(activity, "Устройства не найдены", Toast.LENGTH_LONG).show();
                activity.bar.setVisibility(View.GONE);
                mDiscovering = false;
            }

            @Override
            public void onDiscoveryFailure(Throwable e) {
                Toast.makeText(activity, "Something went wrong while searching for participants", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDiscoverableTimeout() {

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
                    switch (message){
                        case MESSAGE_RESPONSE_ACCEPT_CONNECT:
                            activity.connectedDevice.setVisibility(View.VISIBLE);
                            activity.connectedDevice.setText("Подключено к "+sender.getName());
                            activity.recycler.setVisibility(View.GONE);
                            connectedHosts.add(sender);
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
        nearConnection.send(MESSAGE_REQUEST_CONNECT.getBytes(), host);
    }

    public void stop() {
        nearDiscovery.stopDiscovery();
        nearConnection.stopReceiving(true);
    }

    public void start(String name){
        nearDiscovery.makeDiscoverable(name,"");
        nearDiscovery.startDiscovery();
        if (!nearConnection.isReceiving()) {
            nearConnection.startReceiving();
        }
    }
}
