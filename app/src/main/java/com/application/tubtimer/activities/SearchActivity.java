package com.application.tubtimer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.application.tubtimer.R;
import com.application.tubtimer.adapters.DeviceAdapter;
import com.application.tubtimer.connection.CommandManager;

public class SearchActivity extends AppCompatActivity {
    public DeviceAdapter adapter;
    public RecyclerView recycler;
    public CommandManager commandManager;
    public TextView connectedDevice;

    public final static String DEVICES = "manager";

    public static void start(MainActivity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivityForResult(intent, 1234);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recycler = findViewById(R.id.recycler_devices);
        connectedDevice = findViewById(R.id.connected_device);

        adapter = new DeviceAdapter(this);
        commandManager = new CommandManager(this);
        commandManager.setAdapter(adapter);


        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler.setAdapter(adapter);
    }

    public void onClick(View view) {
        commandManager.nearDiscovery.makeDiscoverable(Settings.Secure.getString(getContentResolver(), "bluetooth_name"));
        commandManager.nearDiscovery.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        getIntent().putExtra(DEVICES,commandManager.connectedHosts);
        commandManager.stop();
        super.onDestroy();
    }
}