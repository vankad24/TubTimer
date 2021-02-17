package com.application.tubtimer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.application.tubtimer.R;
import com.application.tubtimer.adapters.DeviceAdapter;
import com.application.tubtimer.connection.DiscoveryManager;

public class SearchActivity extends AppCompatActivity {
    public DeviceAdapter adapter;
    public RecyclerView recycler;
    public DiscoveryManager discoveryManager;
    public TextView connectedDevice;
    public ProgressBar bar;

    public final static String DEVICES = "manager";

    public static void start(MainActivity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recycler = findViewById(R.id.recycler_devices);
        connectedDevice = findViewById(R.id.connected_device);
        bar = findViewById(R.id.device_bar);

        discoveryManager = new DiscoveryManager(this);
        adapter = new DeviceAdapter(this);
        discoveryManager.setAdapter(adapter);


        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler.setAdapter(adapter);
    }

    public void onClick(View view) {
        discoveryManager.start(Settings.Secure.getString(getContentResolver(), "bluetooth_name"));

        bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        bar.setVisibility(View.GONE);
        discoveryManager.stop();
        Intent intent = new Intent(this,MainActivity.class);
        intent.putParcelableArrayListExtra(DEVICES, discoveryManager.connectedHosts);
        intent.putExtra("int",24);
        finishActivity(1);
        setResult(RESULT_OK, intent);
        finish();
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}