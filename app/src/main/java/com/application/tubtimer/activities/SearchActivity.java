package com.application.tubtimer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.adroitandroid.near.model.Host;
import com.application.tubtimer.R;
import com.application.tubtimer.adapters.DeviceAdapter;
import com.application.tubtimer.connection.Command;
import com.application.tubtimer.connection.CommandManager;

import java.net.InetAddress;

public class SearchActivity extends AppCompatActivity {
    public DeviceAdapter adapter;
    public RecyclerView recycler;
    public CommandManager commandManager;
    public TextView connectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recycler = findViewById(R.id.recycler_devices);
        connectedDevice = findViewById(R.id.connected_device);

        adapter = new DeviceAdapter(this);
        commandManager = new CommandManager(this);

        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler.setAdapter(adapter);
    }

    public void onClick(View view) {
        commandManager.nearDiscovery.makeDiscoverable(Settings.Secure.getString(getContentResolver(), "bluetooth_name"));
        commandManager.nearDiscovery.startDiscovery();
    }
}